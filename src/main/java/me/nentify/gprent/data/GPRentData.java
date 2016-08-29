package me.nentify.gprent.data;

import com.google.common.base.Objects;
import me.nentify.gprent.GPRentType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class GPRentData extends AbstractData<GPRentData, ImmutableGPRentData> {

    private GPRentType type;
    private String claimId;

    public GPRentData() {
        this(null, "");
    }

    public GPRentData(GPRentType type, String claimId) {
        this.type = type;
        this.claimId = claimId;
    }

    public Value<String> gprentType() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.GPRENT_TYPE, type.toString());
    }

    public Value<String> claimId() {
        return Sponge.getRegistry().getValueFactory().createValue(GPRentKeys.CLAIM_ID, claimId);
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(GPRentKeys.GPRENT_TYPE, () -> type);
        registerFieldSetter(GPRentKeys.GPRENT_TYPE, value -> type = GPRentType.valueOf(value));
        registerKeyValue(GPRentKeys.GPRENT_TYPE, this::gprentType);

        registerFieldGetter(GPRentKeys.CLAIM_ID, () -> claimId);
        registerFieldSetter(GPRentKeys.CLAIM_ID, value -> claimId = value);
        registerKeyValue(GPRentKeys.CLAIM_ID, this::claimId);
    }

    @Override
    public Optional<GPRentData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<GPRentData> from(DataContainer container) {
        if (!container.contains(GPRentKeys.CLAIM_ID.getQuery(), GPRentKeys.GPRENT_TYPE.getQuery()))
            return Optional.empty();

        this.claimId = container.getString(GPRentKeys.CLAIM_ID.getQuery()).get();
        this.type = GPRentType.valueOf(container.getString(GPRentKeys.GPRENT_TYPE.getQuery()).get());

        return Optional.of(this);
    }

    @Override
    public GPRentData copy() {
        return new GPRentData(type, claimId);
    }

    @Override
    public ImmutableGPRentData asImmutable() {
        return new ImmutableGPRentData(type, claimId);
    }

    @Override
    public int compareTo(GPRentData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(GPRentKeys.GPRENT_TYPE, type.toString())
                .set(GPRentKeys.CLAIM_ID, claimId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("gprentType", type)
                .add("claimId", claimId)
                .toString();
    }
}
