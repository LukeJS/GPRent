package me.nentify.gprent.claims;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.data.rent.RentData;
import me.ryanhamshire.griefprevention.claim.Claim;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class RentableClaimFactory {

    public static RentableClaim createRentableClaim(Claim claim,
                                                    Location<World> signLocation,
                                                    String name,
                                                    int price,
                                                    int duration,
                                                    Sign sign) {
        CommentedConfigurationNode configNode = GPRent.instance.getRentsConfig().getNode(claim.getID().toString());
        RentableClaim rentableClaim = new RentableClaim(claim, signLocation, name, price, duration, configNode);
        GPRent.instance.getRentableClaims().add(rentableClaim);
        sign.offer(new RentData(claim.getID().toString()));

        return rentableClaim;
    }

    public static RentableClaim loadRentableClaim(Claim claim,
                                           Location<World> signLocation,
                                           String name,
                                           int price,
                                           int duration,
                                           UUID renter,
                                           String renterName,
                                           int rentedAt) {
        CommentedConfigurationNode configNode = GPRent.instance.getRentsConfig().getNode(claim.getID().toString());
        RentableClaim rentableClaim = new RentableClaim(claim,
                signLocation,
                name,
                price,
                duration,
                configNode,
                renter,
                renterName,
                rentedAt);
        GPRent.instance.getRentableClaims().add(rentableClaim);

        return rentableClaim;
    }
}