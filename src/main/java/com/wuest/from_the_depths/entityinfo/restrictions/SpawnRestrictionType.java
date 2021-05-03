package com.wuest.from_the_depths.entityinfo.restrictions;

import net.minecraft.util.ResourceLocation;

public enum SpawnRestrictionType {
    DIMENSION(int[].class),
    TIME_OF_DAY(long.class),
    WEATHER(String.class),
    BIOME(ResourceLocation.class),
    Y_LEVEL(int.class),
    GROUND_RADIUS(int.class);

    private final Class<?> jsonDataClass;

    SpawnRestrictionType(Class<?> jsonDataClass)
    {
        this.jsonDataClass = jsonDataClass;
    }

    public Class<?> getJsonDataClass()
    {
        return jsonDataClass;
    }

}
