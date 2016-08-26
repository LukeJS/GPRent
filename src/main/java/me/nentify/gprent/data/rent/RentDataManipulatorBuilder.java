package me.nentify.gprent.data.rent;

import me.nentify.gprent.data.GPRentKeys;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class RentDataManipulatorBuilder implements DataManipulatorBuilder<RentData, ImmutableRentData> {

    @Override
    public RentData create() {
        return new RentData();
    }

    @Override
    public Optional<RentData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(RentData.class).orElse(new RentData()));
    }

    @Override
    public Optional<RentData> build(DataView container) throws InvalidDataException {
        if (container.contains(GPRentKeys.CLAIM_ID)) {
            String claimId = container.getString(GPRentKeys.CLAIM_ID.getQuery()).get();
            return Optional.of(new RentData(claimId));
        }

        return Optional.empty();
    }
}
