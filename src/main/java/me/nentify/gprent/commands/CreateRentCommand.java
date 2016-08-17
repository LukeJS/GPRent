package me.nentify.gprent.commands;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.claims.RentableClaim;
import me.nentify.gprent.data.rent.RentData;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.claim.Claim;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.trait.IntegerTrait;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class CreateRentCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int price = args.<Integer>getOne("price").get();
        int duration = args.<Integer>getOne("duration").get();
        String name = args.<String>getOne("name").get();

        if (src instanceof Player) {
            Player player = (Player) src;

            BlockRay<World> blockRay = BlockRay.from(player)
                    .blockLimit(100)
                    .filter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .build();

            Optional<BlockRayHit<World>> hitOptional = blockRay.end();

            if (hitOptional.isPresent()) {
                BlockRayHit<World> hit = hitOptional.get();

                Location<World> blockLoc = hit.getLocation();

                player.sendMessage(Text.of(TextColors.AQUA, blockLoc.getBlock().getName()));
                player.sendMessage(Text.of(TextColors.YELLOW, blockLoc.getBlock().getType()));

                Optional<TileEntity> tileEntity = blockLoc.getTileEntity();

                if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                    Sign sign = (Sign) tileEntity.get();
                    Claim claim = GriefPrevention.instance.dataStore.getClaimAtPlayer(player, true);

                    if (!claim.isWildernessClaim()) {
                        RentableClaim rentableClaim = new RentableClaim(claim, blockLoc, name, price, duration);
                        GPRent.instance.getRentableClaims().add(rentableClaim);

                        sign.offer(new RentData(claim.getID().toString()));
                    } else {
                        player.sendMessage(Text.of(TextColors.RED, "You are not standing in a claim"));
                    }
                } else {
                    player.sendMessage(Text.of(TextColors.RED, "You are not looking at a sign"));
                }
            } else {
                player.sendMessage(Text.of(TextColors.RED, "You are not looking at a sign"));
            }
        } else {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to run this command"));
        }

        return CommandResult.success();
    }
}
