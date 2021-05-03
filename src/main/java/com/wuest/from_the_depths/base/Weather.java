package com.wuest.from_the_depths.base;

import net.minecraft.world.storage.WorldInfo;

import java.util.function.Predicate;

/**
 * @author Davoleo
 */
public enum Weather {
    CLEAN(world -> !world.isRaining() && !world.isThundering()),
    RAINING(WorldInfo::isRaining),
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
