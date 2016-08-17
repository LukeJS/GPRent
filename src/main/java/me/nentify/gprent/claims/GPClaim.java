package me.nentify.gprent.claims;

import me.ryanhamshire.griefprevention.claim.Claim;

import java.util.List;
import java.util.UUID;

public class GPClaim {

    private final Claim claim;

    public GPClaim(Claim claim) {
        this.claim = claim;
    }

    public UUID getClaimId() {
        return claim.getID();
    }

    /**
     * Returns true if the user is a builder in the claims
     * @param uuid The user's uuid
     * @return If the user is a builder in the claims
     */
    public boolean hasBuilder(UUID uuid) {
        List<UUID> builders = claim.getClaimData().getBuilders();
        return builders.contains(uuid);
    }

    /**
     * Adds a builder to the GriefPrevention claims
     *
     * @param uuid The user's UUID
     */
    public void addBuilder(UUID uuid) {
        List<UUID> builders = claim.getClaimData().getBuilders();

        if (!builders.contains(uuid))
            builders.add(uuid);

        save();
    }

    public void addManager(UUID uuid) {
        List<UUID> managers = claim.getClaimData().getManagers();

        if (!managers.contains(uuid))
            managers.add(uuid);

        save();
    }

    public void removeManager(UUID uuid) {
        List<UUID> managers = claim.getClaimData().getManagers();

        if (managers.contains(uuid))
            managers.remove(uuid);

        save();
    }

    public void clearManagers() {
        List<UUID> managers = claim.getClaimData().getManagers();
        managers.clear();

        save();
    }

    public void clearAllTrust() {
        claim.getClaimData().getAccessors().clear();
        claim.getClaimData().getBuilders().clear();
        claim.getClaimData().getContainers().clear();
        claim.getClaimData().getManagers().clear();

        save();
    }

    /**
     * Saves the claims after a change has been made
     */
    public void save() {
        if (claim.parent != null)
            claim.parent.getClaimData().setRequiresSave(true);
        else
            claim.getClaimData().setRequiresSave(true);

        claim.getClaimStorage().save();
    }
}
