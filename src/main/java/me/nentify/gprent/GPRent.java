package me.nentify.gprent;

import com.google.inject.Inject;
import me.nentify.gprent.claims.RentableClaims;
import me.nentify.gprent.commands.CreateRentCommand;
import me.nentify.gprent.data.rent.ImmutableRentData;
import me.nentify.gprent.data.rent.RentData;
import me.nentify.gprent.data.rent.RentDataManipulatorBuilder;
import me.nentify.gprent.events.BlockEventHandler;
import me.nentify.gprent.events.PlayerEventHandler;
import me.nentify.gprent.tasks.RentCheckerTask;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.concurrent.TimeUnit;

@Plugin(id = GPRent.PLUGIN_ID, name = GPRent.PLUGIN_NAME, version = GPRent.PLUGIN_VERSION)
public class GPRent {

    public static final String PLUGIN_ID = "gprent";
    public static final String PLUGIN_NAME = "GPRent";
    public static final String PLUGIN_VERSION = "0.0.1";

    public static GPRent instance;

    @Inject
    public Logger logger;

    private RentableClaims rentableClaims = new RentableClaims();

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        logger.info("Starting " + PLUGIN_NAME + " v" + PLUGIN_VERSION);

        instance = this;

        CommandSpec createRentCommand = CommandSpec.builder()
                .description(Text.of("Put the region you're standing in up for rent"))
                .permission("gprent.create")
                .arguments(
                        GenericArguments.integer(Text.of("price")),
                        GenericArguments.integer(Text.of("duration")),
                        GenericArguments.remainingJoinedStrings(Text.of("name"))
                )
                .executor(new CreateRentCommand())
                .build();

        Sponge.getCommandManager().register(this, createRentCommand, "rent");

        Sponge.getDataManager().register(RentData.class, ImmutableRentData.class, new RentDataManipulatorBuilder());

        Sponge.getGame().getEventManager().registerListeners(this, new PlayerEventHandler());
        Sponge.getGame().getEventManager().registerListeners(this, new BlockEventHandler());

        Task task = Sponge.getScheduler().createTaskBuilder()
                .execute(new RentCheckerTask())
                .delay(5, TimeUnit.SECONDS)
                .interval(5, TimeUnit.SECONDS)
                .name("GPRent - Rent Checker")
                .submit(this);
    }

    public RentableClaims getRentableClaims() {
        return rentableClaims;
    }
}
