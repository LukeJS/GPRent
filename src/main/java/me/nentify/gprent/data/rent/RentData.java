package me.nentify.gprent.data.rent;

import com.google.common.base.Objects;
import me.nentify.gprent.data.GPRentKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class RentData extends AbstractData<RentData, ImmutableRentData> {

    private String claimId;

    public RentData() {
        this("");
    }

    public RentData(String claimId) {
        this.claimId = claimId;
    }

    public Value<String> claimId() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.CLAIM_ID, claimId);
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(GPRentKeys.CLAIM_ID, () -> claimId);
        registerFieldSetter(GPRentKeys.CLAIM_ID, value -> claimId = value);
        registerKeyValue(GPRentKeys.CLAIM_ID, this::claimId);
    }

    @Override
    public Optional<RentData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<RentData> from(DataContainer container) {
        if (!container.contains(GPRentKeys.CLAIM_ID.getQuery()))
            return Optional.empty();

        this.claimId = container.getString(GPRentKeys.CLAIM_ID.getQuery()).get();

        return Optional.of(this);
    }

    @Override
    public RentData copy() {
        return new RentData(claimId);
    }

    @Override
    public ImmutableRentData asImmutable() {
        return new ImmutableRentData(claimId);
    }

    @Override
    public int compareTo(RentData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(GPRentKeys.CLAIM_ID, claimId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("claimId", claimId)
                .toString();
    }
}
