package me.nentify.gprent.commands;

import me.nentify.gprent.GPRent;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.claim.Claim;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.extent.Extent;

import java.util.function.Predicate;

public class LetCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        double price = args.<Double>getOne("price").get();
        int duration = args.<Integer>getOne("duration").get();
        String name = args.<String>getOne("name").get();

        if (src instanceof Player) {
            Player player = (Player) src;

            Claim claim = GriefPrevention.instance.dataStore.getClaimAtPlayer(player, true);

            if (!claim.isWildernessClaim()) {
                LetCommandData letCommandData = new LetCommandData(claim, name, price, duration);
                GPRent.addLetcommandData(player.getUniqueId(), letCommandData);

                player.sendMessage(Text.of(TextColors.YELLOW, "Right click the sign you would like to display the rent data on"));
            } else {
                player.sendMessage(Text.of(TextColors.RED, "You are not standing in a claim"));
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

    public static class LetCommandData {
        public final Claim claim;
        public final String name;
        public final double price;
        public final int duration;

        public LetCommandData(Claim claim, String name, double price, int duration) {
            this.claim = claim;
            this.name = name;
            this.price = price;
            this.duration = duration;
        }
    }
}
