package me.nentify.gprent;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.nentify.gprent.claims.RentableClaim;
import me.nentify.gprent.claims.RentableClaims;
import me.nentify.gprent.commands.GPRentCommand;
import me.nentify.gprent.data.ImmutableGPRentData;
import me.nentify.gprent.data.GPRentData;
import me.nentify.gprent.data.GPRentDataManipulatorBuilder;
import me.nentify.gprent.events.BlockEventHandler;
import me.nentify.gprent.tasks.RentCheckerTask;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.claim.Claim;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin(id = GPRent.PLUGIN_ID, name = GPRent.PLUGIN_NAME)
public class GPRent {

    public static final String PLUGIN_ID = "gprent";
    public static final String PLUGIN_NAME = "GPRent";

    public static GPRent instance;

    @Inject
    public Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private Path rentsFile;

    private ConfigurationLoader<CommentedConfigurationNode> rentsLoader;
    private ConfigurationNode rentsConfig;

    private static RentableClaims rentableClaims = new RentableClaims();

    public EconomyService economyService;

    // Stores rent data after a player uses the /gprent command to be later put on to a sign placed by the player
    public static Map<UUID, GPRentCommand.Data> gprentCommandData = new HashMap<>();

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;

        try {
            loadConfigs();
        } catch (IOException e) {
            logger.error("Failed to load config files");
            e.printStackTrace();
        }

        CommandSpec gprentCommand = CommandSpec.builder()
                .description(Text.of("Put the region you're standing in up for rent"))
                .permission("gprent.create")
                .arguments(
                        GenericArguments.enumValue(Text.of("type"), GPRentType.class),
                        GenericArguments.doubleNum(Text.of("price")),
                        GenericArguments.integer(Text.of("duration")),
                        GenericArguments.remainingJoinedStrings(Text.of("name"))
                )
                .executor(new GPRentCommand())
                .build();

        Sponge.getCommandManager().register(this, gprentCommand, "gprent");

        Sponge.getDataManager().register(GPRentData.class, ImmutableGPRentData.class, new GPRentDataManipulatorBuilder());

        Sponge.getGame().getEventManager().registerListeners(this, new BlockEventHandler());

        Task task = Sponge.getScheduler().createTaskBuilder()
                .execute(new RentCheckerTask())
                .delay(5, TimeUnit.SECONDS)
                .interval(5, TimeUnit.SECONDS)
                .name("GPRent - Rent Checker")
                .submit(this);
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        try {
            loadRentableClaims();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static RentableClaims getRentableClaims() {
        return rentableClaims;
    }

    private void loadConfigs() throws IOException {
        if (!Files.exists(configDir))
            Files.createDirectories(configDir);

        rentsFile = configDir.resolve("rents.conf");

        if (!Files.exists(rentsFile))
            Files.createFile(rentsFile);

        rentsLoader = HoconConfigurationLoader.builder().setPath(rentsFile).build();
        rentsConfig = rentsLoader.load();
    }

    private void loadRentableClaims() throws ObjectMappingException {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : rentsConfig.getChildrenMap().entrySet()) {
            ConfigurationNode node = entry.getValue();

            UUID claimId = UUID.fromString((String) entry.getKey());
            UUID claimWorldId = node.getNode("claimWorld").getValue(TypeToken.of(UUID.class));

            String name = node.getNode("name").getString();
            double price = node.getNode("price").getDouble();
            int duration = node.getNode("duration").getInt();

            Optional<World> claimWorld = Sponge.getServer().getWorld(claimWorldId);

            if (claimWorld.isPresent()) {
                ConfigurationNode renterNode = node.getNode("renter");

                UUID renter = null;
                String renterName = null;
                int rentedAt = -1;

                if (!renterNode.isVirtual()) {
                    renter = node.getNode("renter").getValue(TypeToken.of(UUID.class));
                    renterName = node.getNode("renterName").getString();
                    rentedAt = node.getNode("rentedAt").getInt();
                }

                ConfigurationNode signLocationNode = node.getNode("signLocation");

                UUID worldUuid = signLocationNode.getNode("world").getValue(TypeToken.of(UUID.class));
                Optional<World> world = Sponge.getServer().getWorld(worldUuid);

                if (world.isPresent()) {
                    int x = signLocationNode.getNode("x").getInt();
                    int y = signLocationNode.getNode("y").getInt();
                    int z = signLocationNode.getNode("z").getInt();

                    Location<World> signLocation = new Location<World>(world.get(), x, y, z);

                    Claim claim = GriefPrevention.instance.dataStore.getClaim(claimWorld.get().getProperties(), claimId);

                    if (claim != null) {
                        RentableClaim rentableClaim = new RentableClaim(claim, signLocation, name, price, duration, rentsConfig.getNode(claim.getID().toString()), renter, renterName, rentedAt);
                        rentableClaims.add(rentableClaim);
                    } else {
                        logger.error("Failed to find claim with UUID: " + claimId);
                        getRentsConfig().removeChild(claimId.toString());
                    }
                } else {
                    logger.error("Could not find world with UUID " + worldUuid + " for sign location " + name + " with claim ID " + claimId);
                }
            } else {
                logger.error("Could not find world with UUID " + claimWorldId + " for claim " + name + " with claim ID " + claimId);
            }
        }

    }

    public ConfigurationLoader<CommentedConfigurationNode> getRentsLoader() {
        return rentsLoader;
    }

    public ConfigurationNode getRentsConfig() {
        return rentsConfig;
    }

    public void saveConfig() {
        try {
            rentsLoader.save(rentsConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if (event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    public static void addGPRentCommandData(UUID uuid, GPRentCommand.Data data) {
        gprentCommandData.put(uuid, data);
    }

    public static Optional<GPRentCommand.Data> takeGPRentCommandData(UUID uuid) {
        if (gprentCommandData.containsKey(uuid)) {
            GPRentCommand.Data data = gprentCommandData.get(uuid);
            gprentCommandData.remove(uuid);
            return Optional.of(data);
        }

        return Optional.empty();
    }
}
