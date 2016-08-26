package me.nentify.gprent.claims;

import com.google.common.reflect.TypeToken;
import me.nentify.gprent.GPRent;
import me.nentify.gprent.Utils;
import me.ryanhamshire.griefprevention.claim.Claim;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class RentableClaim extends GPClaim {

    private Location<World> signLocation;

    private String name;
    private double price;
    private int duration;

    private UUID renter;
    private String renterName;
    private int rentedAt;

    private CommentedConfigurationNode configNode;

    public RentableClaim(Claim claim, Location<World> signLocation, String name, double price, int duration, CommentedConfigurationNode configNode) {
        this(claim, signLocation, name, price, duration, configNode, null, null, -1);
    }

    public RentableClaim(Claim claim, Location<World> signLocation, String name, double price, int duration, CommentedConfigurationNode configNode, UUID renter, String renterName, int rentedAt) {
        super(claim);

        this.signLocation = signLocation;

        this.name = name;
        this.price = price;
        this.duration = duration;

        this.signLocation = signLocation;

        this.renter = renter;
        this.renterName = renterName;
        this.rentedAt = rentedAt;

        this.configNode = configNode;

        updateSign();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateSign();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        updateSign();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        updateSign();
    }

    public Optional<UUID> getRenter() {
        if (isRented())
            return Optional.of(renter);

        return Optional.empty();
    }

    public void setRenter(UUID renter, String renterName) {
        addManager(renter);

        this.renter = renter;
        this.renterName = renterName;
        this.rentedAt = Utils.getCurrentUnixTimestamp();

        updateSign();
    }

    public void removeRenter() {
        clearAllTrust();

        this.renter = null;
        this.rentedAt = -1;

        updateSign();
    }

    public boolean isRented() {
        return renter != null;
    }

    public Optional<String> getRenterName() {
        if (isRented())
            return Optional.of(renterName);

        return Optional.empty();
    }

    public Optional<Integer> getRentedAt() {
        if (isRented())
            return Optional.of(rentedAt);

        return Optional.empty();
    }

    public boolean hasExpired() {
        if (isRented())
            return getRemainingTime().get() < 0;

        return true;
    }

    public Optional<Integer> getRemainingTime() {
        if (isRented())
            return Optional.of(duration - (Utils.getCurrentUnixTimestamp() - rentedAt));

        return Optional.empty();
    }

    public void check() {
        if (hasExpired()) {
            removeRenter();
        } else {
            updateSign();
        }
    }

    public void updateSign() {
        Optional<TileEntity> tileEntity = signLocation.getTileEntity();

        if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
            Sign sign = (Sign) tileEntity.get();

            Optional<SignData> signDataOptional = sign.getOrCreate(SignData.class);

            if (signDataOptional.isPresent()) {
                SignData signData = signDataOptional.get();
                ListValue<Text> lines = signData.lines();

                if (isRented()) {
                    lines.set(0, Text.of(TextColors.DARK_RED, "[Rented]"));
                    lines.set(1, Text.of(TextColors.BLACK, name));
                    lines.set(2, Text.of(TextColors.BLACK, getRenterName().get()));
                    lines.set(3, Text.of(TextColors.BLACK, prettyTime(getRemainingTime().get()) + " left"));
                } else {
                    lines.set(0, Text.of(TextColors.DARK_BLUE, "[Rent]"));
                    lines.set(1, Text.of(TextColors.BLACK, name));
                    lines.set(2, Text.of(TextColors.BLACK, "$" + price));
                    lines.set(3, Text.of(TextColors.BLACK, prettyTime(duration)));
                }

                sign.offer(lines);
            }
        } else {
            GPRent.instance.logger.info("Invalid sign for rentable claim with claim ID: " + getClaimId());
        }
    }

    public String prettyTime(int seconds) {
        if (seconds < 60)
            return plural(seconds, "sec");

        int minutes = seconds / 60;

        if (minutes < 60)
            return plural(minutes, "min");

        int hours = minutes / 60;

        if (hours < 24)
            return plural(hours, "hour");

        int days = hours / 24;

        if (days < 356)
            return plural(days, "day");

        int years = days / 356;
        return plural(years, "year");
    }

    public String plural(int time, String suffix) {
        if (time == 1)
            return time + " " + suffix;

        return time + " " + suffix + "s";
    }

    public void saveConfig() {
        try {
            configNode.getNode("claimWorld").setValue(TypeToken.of(UUID.class), getWorld().getUniqueId());

            configNode.getNode("name").setValue(name);
            configNode.getNode("price").setValue(price);
            configNode.getNode("duration").setValue(duration);

            configNode.getNode("renter").setValue(getRenter().orElse(null));
            configNode.getNode("renterName").setValue(getRenterName().orElse(null));
            configNode.getNode("rentedAt").setValue(getRentedAt().orElse(null));

            CommentedConfigurationNode signLocationNode = configNode.getNode("signLocation");

            signLocationNode.getNode("world").setValue(TypeToken.of(UUID.class), signLocation.getExtent().getUniqueId());
            signLocationNode.getNode("x").setValue(signLocation.getX());
            signLocationNode.getNode("y").setValue(signLocation.getY());
            signLocationNode.getNode("z").setValue(signLocation.getZ());

            GPRent.instance.saveConfig();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        GPRent.getRentableClaims().remove(this);
        configNode.setValue(null);
        GPRent.instance.saveConfig();
    }
}
