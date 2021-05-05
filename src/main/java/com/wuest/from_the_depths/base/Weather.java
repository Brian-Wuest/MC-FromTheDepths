package com.wuest.from_the_depths.base;

import com.google.gson.annotations.SerializedName;
import net.minecraft.world.storage.WorldInfo;

import java.util.function.Predicate;

/**
 * @author Davoleo
 */
public enum Weather {
    @SerializedName(value = "clean", alternate = {"CLEAN", "Clean"})
    CLEAN(world -> !world.isRaining() && !world.isThundering()),
    @SerializedName(value = "rain", alternate = {"RAIN", "Rain", "RAINING", "raining"})
    RAINING(WorldInfo::isRaining),
    @SerializedName(value = "storm", alternate = {"STORM", "Storm", "thundering", "THUNDERING"})
    STORM(WorldInfo::isThundering);

    private final Predicate<WorldInfo> isCurrentState;

    Weather(Predicate<WorldInfo> isCurrentState)
    {
        this.isCurrentState = isCurrentState;
    }

    public boolean isCurrentState(WorldInfo worldInfo)
    {
        return isCurrentState.test(worldInfo);
    }
}
