package me.nentify.gprent.claims;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.Utils;
import me.ryanhamshire.griefprevention.claim.Claim;
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
    private int price;
    private int duration;

    private Optional<UUID> renter = Optional.empty();
    private Optional<String> renterName = Optional.empty();
    private Optional<Integer> rentedAt = Optional.empty();

    public RentableClaim(Claim claim, Location<World> signLocation, String name, int price, int duration) {
        super(claim);

        this.name = name;
        this.price = price;
        this.duration = duration;

        this.signLocation = signLocation;

        updateSign();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateSign();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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
        return renter;
    }

    public void addRenter(UUID renter, String renterName) {
        addManager(renter);

        this.renter = Optional.of(renter);
        this.renterName = Optional.of(renterName);
        this.rentedAt = Optional.of(Utils.getCurrentUnixTimestamp());

        updateSign();
    }

    public void removeRenter() {
        clearAllTrust();

        this.renter = Optional.empty();
        this.rentedAt = Optional.empty();

        updateSign();
    }

    public boolean isRented() {
        return renter.isPresent();
    }

    public Optional<String> getRenterName() {
        return renterName;
    }

    public boolean hasExpired() {
        if (isRented())
            return getRemainingTime().get() < 0;

        return true;
    }

    public Optional<Integer> getRemainingTime() {
        if (isRented())
            return Optional.of(duration - (Utils.getCurrentUnixTimestamp() - rentedAt.get()));

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
}
