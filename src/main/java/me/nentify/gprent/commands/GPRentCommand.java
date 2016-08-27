package me.nentify.gprent.commands;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.GPRentType;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.claim.Claim;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GPRentCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        GPRentType type = args.<GPRentType>getOne("type").get();
        double price = args.<Double>getOne("price").get();
        int duration = args.<Integer>getOne("duration").get();
        String name = args.<String>getOne("name").get();

        if (src instanceof Player) {
            Player player = (Player) src;

            Claim claim = GriefPrevention.instance.dataStore.getClaimAtPlayer(player, true);

            if (GPRent.getRentableClaims().has(claim.getID())) {
                player.sendMessage(Text.of(TextColors.RED, "This claim is already for rent"));
                return CommandResult.success();
            }

            if (!claim.isWildernessClaim()) {
                Data gprentData = new Data(type, claim, name, price, duration);
                GPRent.addGPRentCommandData(player.getUniqueId(), gprentData);

                player.sendMessage(Text.of(TextColors.YELLOW, "Right click the sign you would like to display the rent data on"));
            } else {
                player.sendMessage(Text.of(TextColors.RED, "You are not standing in a claim"));
            }
        } else {
            src.sendMessage(Text.of(TextColors.RED, "You must be a player to run this command"));
        }

        return CommandResult.success();
    }

    public static class Data {
        public final GPRentType type;
        public final Claim claim;
        public final String name;
        public final double price;
        public final int duration;

        public Data(GPRentType type, Claim claim, String name, double price, int duration) {
            this.type = type;
            this.claim = claim;
            this.name = name;
            this.price = price;
            this.duration = duration;
        }
    }
}
