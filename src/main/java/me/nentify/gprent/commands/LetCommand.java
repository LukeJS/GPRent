package me.nentify.gprent.commands;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.claims.RentableClaim;
import me.nentify.gprent.claims.RentableClaimFactory;
import me.nentify.gprent.data.rent.RentData;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.claim.Claim;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
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
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class LetCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int price = args.<Integer>getOne("price").get();
        int duration = args.<Integer>getOne("duration").get();
        String name = args.<String>getOne("name").get();

        if (src instanceof Player) {
            Player player = (Player) src;

            BlockRay<World> blockRay = BlockRay.from(player)
                    .blockLimit(100)
                    .filter(signFilter())
                    .build();

            Optional<BlockRayHit<World>> hitOptional = blockRay.end();

            if (hitOptional.isPresent()) {
                BlockRayHit<World> hit = hitOptional.get();
                Location<World> blockLoc = hit.getLocation();
                Optional<TileEntity> tileEntity = blockLoc.getTileEntity();

                if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                    Sign sign = (Sign) tileEntity.get();
                    Claim claim = GriefPrevention.instance.dataStore.getClaimAtPlayer(player, true);

                    if (!claim.isWildernessClaim()) {
                        RentableClaim rentableClaim = RentableClaimFactory.createRentableClaim(claim, blockLoc, name, price, duration, sign);
                        rentableClaim.saveConfig();

                        player.sendMessage(Text.of(TextColors.GREEN, "Successfully let plot " + name));
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

    public static <E extends Extent> Predicate<BlockRayHit<E>> signFilter() {
        return lastHit -> isSign(lastHit.getExtent().getBlockType(lastHit.getBlockX(), lastHit.getBlockY(), lastHit.getBlockZ()));
    }

    public static boolean isSign(BlockType type) {
        return type == BlockTypes.STANDING_SIGN || type == BlockTypes.WALL_SIGN;
    }
}
