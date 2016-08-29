package me.nentify.gprent.data;

import me.nentify.gprent.GPRentType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableGPRentData extends AbstractImmutableData<ImmutableGPRentData, GPRentData> {

    private GPRentType type;
    private String claimId;

    public ImmutableGPRentData() {
        this(null, "");
    }

    public ImmutableGPRentData(GPRentType type, String claimId) {
        this.type = type;
        this.claimId = claimId;
    }

    public ImmutableValue<String> gprentType() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.GPRENT_TYPE, type.toString()).asImmutable();
    }

    public ImmutableValue<String> claimId() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.CLAIM_ID, claimId).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(GPRentKeys.GPRENT_TYPE, () -> type);
        registerKeyValue(GPRentKeys.GPRENT_TYPE, this::gprentType);

        registerFieldGetter(GPRentKeys.CLAIM_ID, () -> claimId);
        registerKeyValue(GPRentKeys.CLAIM_ID, this::claimId);
    }

    @Override
    public GPRentData asMutable() {
        return new GPRentData(type, claimId);
    }

    @Override
    public int compareTo(ImmutableGPRentData o) {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(GPRentKeys.GPRENT_TYPE, type.toString())
                .set(GPRentKeys.CLAIM_ID, claimId);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public String getClaimId() {
        return claimId;
    }
}
