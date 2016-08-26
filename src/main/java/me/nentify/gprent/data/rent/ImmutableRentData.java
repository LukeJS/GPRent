package me.nentify.gprent.data.rent;

import me.nentify.gprent.data.GPRentKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableRentData extends AbstractImmutableData<ImmutableRentData, RentData> {

    private String claimId;

    public ImmutableRentData() {
        this("");
    }

    public ImmutableRentData(String claimId) {
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
    public RentData asMutable() {
        return new RentData(claimId);
    }

    @Override
    public int compareTo(ImmutableRentData o) {
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
