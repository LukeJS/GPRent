package me.nentify.gprent.data;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class GPRentDataManipulatorBuilder implements DataManipulatorBuilder<GPRentData, ImmutableGPRentData> {

    @Override
    public GPRentData create() {
        return new GPRentData();
    }

    @Override
    public Optional<GPRentData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(GPRentData.class).orElse(new GPRentData()));
    }

    @Override
    public Optional<GPRentData> build(DataView container) throws InvalidDataException {
        if (container.contains(GPRentKeys.CLAIM_ID)) {
            String claimId = container.getString(GPRentKeys.CLAIM_ID.getQuery()).get();
            return Optional.of(new GPRentData(claimId));
        }

        return Optional.empty();
    }
}
