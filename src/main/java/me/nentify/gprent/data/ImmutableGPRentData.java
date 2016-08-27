package me.nentify.gprent.data;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableGPRentData extends AbstractImmutableData<ImmutableGPRentData, GPRentData> {

    private String claimId;

    public ImmutableGPRentData() {
        this("");
    }

    public ImmutableGPRentData(String claimId) {
        this.claimId = claimId;
    }

    public ImmutableValue<String> claimId() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.CLAIM_ID, claimId).asImmutable();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(GPRentKeys.CLAIM_ID, () -> claimId);
        registerKeyValue(GPRentKeys.CLAIM_ID, this::claimId);
    }

    @Override
    public GPRentData asMutable() {
        return new GPRentData(claimId);
    }

    @Override
    public int compareTo(ImmutableGPRentData o) {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
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
