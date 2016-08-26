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
     *
     *
     * Add a RentableClaim
     * @param rentableClaim The RentableClaim
     */
    public void add(RentableClaim rentableClaim) {
        rentableClaims.put(rentableClaim.getClaimId(), rentableClaim);
    }

    public Collection<RentableClaim> all() {
        return rentableClaims.values();
    }

    public void remove(RentableClaim rentableClaim) {
        if (rentableClaims.containsKey(rentableClaim.getClaimId()))
            rentableClaims.remove(rentableClaim.getClaimId());
    }

    public boolean has(UUID claimUuid) {
        return rentableClaims.containsKey(claimUuid);
    }
}
