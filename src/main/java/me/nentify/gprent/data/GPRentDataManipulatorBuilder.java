package me.nentify.gprent.data;

import me.nentify.gprent.GPRentType;
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
        if (container.contains(GPRentKeys.CLAIM_ID, GPRentKeys.GPRENT_TYPE)) {
            GPRentType type = GPRentType.valueOf(container.getString(GPRentKeys.GPRENT_TYPE.getQuery()).get());
            String claimId = container.getString(GPRentKeys.CLAIM_ID.getQuery()).get();
            return Optional.of(new GPRentData(type, claimId));
        }

        return Optional.empty();
    }
}
