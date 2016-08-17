package me.nentify.gprent.claims;

import java.util.*;

public class RentableClaims {

    private Map<UUID, RentableClaim> rentableClaims = new HashMap<>();

    /**
     * Returns the RentableClaim for a GriefPrevention Claim if one exists
     * @param claimId The GriefPrevention claim ID
     * @return The RentableClaim
     */
    public Optional<RentableClaim> get(UUID claimId) {
        if (rentableClaims.containsKey(claimId))
            return Optional.of(rentableClaims.get(claimId));

        return Optional.empty();
    }

    /**
     * Add a RentableClaim
     * @param rentableClaim The RentableClaim
     */
    public void add(RentableClaim rentableClaim) {
        if (!rentableClaims.containsKey(rentableClaim.getClaimId()))
            rentableClaims.put(rentableClaim.getClaimId(), rentableClaim);
    }

    public Collection<RentableClaim> all() {
        return rentableClaims.values();
    }
}
