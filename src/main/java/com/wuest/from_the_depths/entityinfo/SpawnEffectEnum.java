package com.wuest.from_the_depths.entityinfo;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.IStringSerializable;

public enum SpawnEffectEnum implements IStringSerializable {

    @SerializedName(value = "none", alternate = {"NONE", "None"})
    NONE("none"),

    @SerializedName(value = "lightning", alternate = {"LIGHTNING", "Lightning"})
    LIGHTNING("lightning");

    private final String name;

    SpawnEffectEnum(String name) {
        this.name = name;
    }

    public static SpawnEffectEnum getFromName(String name) {
        SpawnEffectEnum returnValue = SpawnEffectEnum.NONE;

        if (name != null && name.toLowerCase().equals(SpawnEffectEnum.LIGHTNING.getName())) {
            returnValue = SpawnEffectEnum.LIGHTNING;
        }

        return returnValue;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
