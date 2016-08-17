package me.nentify.gprent.events;

import me.nentify.gprent.GPRent;
import me.nentify.gprent.claims.RentableClaim;
import me.nentify.gprent.data.rent.RentData;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class BlockEventHandler {

    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        Optional<Location<World>> blockLoc = event.getTargetBlock().getLocation();

        if (blockLoc.isPresent()) {
            Optional<TileEntity> tileEntity = blockLoc.get().getTileEntity();

            if (tileEntity.isPresent() && tileEntity.get() instanceof Sign) {
                Sign sign = (Sign) tileEntity.get();

                Optional<RentData> rentDataOptional = sign.getOrCreate(RentData.class);

                if (rentDataOptional.isPresent()) {
                    RentData rentData = rentDataOptional.get();

                    UUID uuid = UUID.fromString(rentData.claimId().get());

                    Optional<RentableClaim> rentableClaimOptional = GPRent.instance.getRentableClaims().get(uuid);

                    if (rentableClaimOptional.isPresent()) {
                        RentableClaim rentableClaim = rentableClaimOptional.get();

                        if (!rentableClaim.isRented()) {
                            rentableClaim.addRenter(player.getUniqueId(), player.getName());
                            player.sendMessage(Text.of(TextColors.GREEN, "You have rented " + rentableClaim.getName()));
                        } else {
                            player.sendMessage(Text.of(TextColors.AQUA, rentableClaim.getName() + " is currently rented by " + rentableClaim.getRenter().get()));
                        }
                    } else {
                        GPRent.instance.logger.error("Sign has rent data but there is no rentable claim. Claim ID: " + uuid);
                    }
                } else {
                    player.sendMessage(Text.of("no rent data"));
                }
            } else {

                player.sendMessage(Text.of("not sign"));
            }
        }
    }
}
