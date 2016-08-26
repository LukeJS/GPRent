package me.nentify.gprent.events;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.claims.RentableClaim;
import me.nentify.gprent.commands.LetCommand;
import me.nentify.gprent.data.GPRentKeys;
import me.nentify.gprent.data.rent.ImmutableRentData;
import me.nentify.gprent.data.rent.RentData;
import me.ryanhamshire.griefprevention.claim.Claim;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BlockEventHandler {

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        Optional<Location<World>> blockLoc = event.getTargetBlock().getLocation();

        if (blockLoc.isPresent()) {
            Optional<TileEntity> tileEntity = blockLoc.get().getTileEntity();

            if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                Sign sign = (Sign) tileEntity.get();

                Optional<RentData> rentDataOptional = sign.getOrCreate(RentData.class);

                if (rentDataOptional.isPresent()) {
                    RentData rentData = rentDataOptional.get();

                    UUID uuid = UUID.fromString(rentData.claimId().get());

                    Optional<RentableClaim> rentableClaimOptional = GPRent.instance.getRentableClaims().get(uuid);

                    if (rentableClaimOptional.isPresent()) {
                        RentableClaim rentableClaim = rentableClaimOptional.get();

                        if (!rentableClaim.isRented()) {
                            double price = rentableClaim.getPrice();

                            Optional<UniqueAccount> account = GPRent.instance.economyService.getOrCreateAccount(player.getUniqueId());

                            if (account.isPresent()) {
                                TransactionResult result = account.get().withdraw(
                                        GPRent.instance.economyService.getDefaultCurrency(),
                                        BigDecimal.valueOf(price),
                                        Cause.source(GPRent.instance).build());

                                if (result.getResult() == ResultType.SUCCESS) {
                                    rentableClaim.setRenter(player.getUniqueId(), player.getName());
                                    player.sendMessage(Text.of(TextColors.GREEN, "You have rented " + rentableClaim.getName(), " for ", GPRent.instance.economyService.getDefaultCurrency().getSymbol(), rentableClaim.getPrice()));
                                } else if (result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {
                                    player.sendMessage(Text.of(TextColors.RED, "You do not have enough money to rent this claim!"));
                                } else {
                                    player.sendMessage(Text.of(TextColors.RED, "Error attempting to withdraw money for this plot. Contact an owner."));
                                }
                            }
                        } else {
                            player.sendMessage(Text.of(TextColors.AQUA, rentableClaim.getName(), " is currently rented by ", rentableClaim.getRenterName().get()));
                        }
                    } else {
                        GPRent.instance.logger.error("Sign has rent data but there is no rentable claim, removing rent data from sign. Claim ID: ", uuid);
                        sign.remove(RentData.class);
                    }
                } else {
                    Optional<LetCommand.LetCommandData> letCommandDataOptional = GPRent.takePlayerShopData(player.getUniqueId());

                    if (letCommandDataOptional.isPresent()) {
                        LetCommand.LetCommandData letCommandData = letCommandDataOptional.get();

                        Claim claim = letCommandData.claim;
                        String name = letCommandData.name;
                        double price = letCommandData.price;
                        int duration = letCommandData.duration;

                        Location<World> signLocation = blockLoc.get();
                        ConfigurationNode configNode = GPRent.instance.getRentsConfig().getNode(claim.getID().toString());

                        RentableClaim rentableClaim = new RentableClaim(claim, signLocation, name, price, duration, configNode);
                        rentableClaim.saveConfig();
                        GPRent.getRentableClaims().add(rentableClaim);

                        sign.offer(new RentData(claim.getID().toString()));

                        player.sendMessage(Text.of(TextColors.GREEN, "Successfully let plot ", name));
                    }
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        List<Transaction<BlockSnapshot>> transactions = event.getTransactions();

        for (Transaction<BlockSnapshot> transaction : transactions) {
            //Optional<Location<World>> blockLoc = transaction.getOriginal().getLocation();

            //GPRent.instance.logger.info(blockLoc.toString());

            //if (blockLoc.isPresent()) {
                //Optional<TileEntity> tileEntity = blockLoc.get().getTileEntity();

                //GPRent.instance.logger.info(tileEntity.toString());

                //if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                    //Sign sign = (Sign) tileEntity.get();

                    //GPRent.instance.logger.info(sign.toString());

                    //Optional<RentData> rentDataOptional = sign.getOrCreate(RentData.class);

            Optional<List<Text>> texts = transaction.getOriginal().get(Keys.SIGN_LINES);
            System.out.println(texts);

            Optional<String> test = transaction.getOriginal().get(GPRentKeys.CLAIM_ID);
            System.out.println(test);

            Set<Key<?>> keys = transaction.getOriginal().getKeys();
            System.out.println(keys);

            List<ImmutableDataManipulator<?, ?>> a = transaction.getOriginal().getContainers();
            System.out.println(a);

            Optional<ImmutableRentData> immutableRentData = transaction.getOriginal().get(ImmutableRentData.class);

            if (immutableRentData.isPresent()) {
                GPRent.instance.logger.info(immutableRentData.get().getClaimId());

                UUID uuid = UUID.fromString(immutableRentData.get().getClaimId());

                Optional<RentableClaim> rentableClaimOptional = GPRent.getRentableClaims().get(uuid);

                if (rentableClaimOptional.isPresent()) {
                            RentableClaim rentableClaim = rentableClaimOptional.get();
                            rentableClaim.delete();
                        }
            }


//            if (claimUuid.isPresent()) {
//                        GPRent.instance.logger.info(claimUuid.get());
//
//                        UUID uuid = UUID.fromString(claimUuid.get());
//
//                        Optional<RentableClaim> rentableClaimOptional = GPRent.getRentableClaims().get(uuid);
//
//                        if (rentableClaimOptional.isPresent()) {
//                            RentableClaim rentableClaim = rentableClaimOptional.get();
//                            rentableClaim.delete();
//                        }
//                    }



                //}
            //}
        }
    }
}
