package me.nentify.gprent.data.rent;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

public class RentKeys {

    public static final Key<Value<String>> CLAIM_ID = KeyFactory.makeSingleKey(String.class, Value.class, DataQuery.of("ClaimId"));
}
