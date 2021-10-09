package com.wuest.from_the_depths.base;

import com.google.gson.annotations.SerializedName;
import net.minecraft.world.storage.WorldInfo;

import java.util.function.Predicate;

public enum Weather {
    @SerializedName(value = "clear", alternate = {"CLEAR", "Clear", "clean", "CLEAN", "Clean"})
    CLEAR("Clear", world -> !world.isRaining() && !world.isThundering()),

    @SerializedName(value = "rain", alternate = {"RAIN", "Rain", "RAINING", "raining"})
    RAINING("Raining", WorldInfo::isRaining),

    @SerializedName(value = "storm", alternate = {"STORM", "Storm", "thundering", "THUNDERING"})
    STORM("Storm", WorldInfo::isThundering);

    private final Predicate<WorldInfo> isCurrentState;
    private final String name;

    Weather(String name, Predicate<WorldInfo> isCurrentState) {
        this.isCurrentState = isCurrentState;
        this.name = name;
    }

    public boolean isCurrentState(WorldInfo worldInfo) {
        return isCurrentState.test(worldInfo);
    }

    public String getName() {
        return this.name;
    }
}
