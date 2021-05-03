package com.wuest.from_the_depths.entityinfo.restrictions;

import com.wuest.from_the_depths.Utilities;
import com.wuest.from_the_depths.base.Weather;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiPredicate;

/**
 * @author Davoleo
 */
public class SpawnRestrictions {

    //Current Dimension
    public static final BiPredicate<World, Integer[]> DIMENSION = (world, dimensions) -> ArrayUtils.contains(dimensions, world.provider.getDimension());

    //World Time of Day
    public static final BiPredicate<World, Long> TIME_OF_DAY_GREATER = (world, time) -> world.provider.getWorldTime() > time;
    public static final BiPredicate<World, Long> TIME_OF_DAY_LESS = (world, time) -> world.provider.getWorldTime() < time;

    //Weather
    public static final BiPredicate<World, Weather> WEATHER = (world, weather) -> weather.isCurrentState(world.getWorldInfo());

    //Biome
    public static final BiPredicate<Tuple<BlockPos, World>, ResourceLocation[]> BIOME = (blockPosWorldPair, biomes) -> {
        Biome biome = blockPosWorldPair.getSecond().provider.getBiomeForCoords(blockPosWorldPair.getFirst());
        return ArrayUtils.contains(biomes, biome.getRegistryName());
    };

    // Y Level
    public static final BiPredicate<BlockPos, Integer> Y_LEVEL_EQUALS = (blockPos, yLevel) -> blockPos.getY() == yLevel;
    public static final BiPredicate<BlockPos, Integer> Y_LEVEL_GREATER = (blockPos, yLevel) -> blockPos.getY() > yLevel;
    public static final BiPredicate<BlockPos, Integer> Y_LEVEL_LESS = (blockPos, yLevel) -> blockPos.getY() < yLevel;

    // Ground Radius
    public static final BiPredicate<Tuple<BlockPos, World>, Integer> GROUND_RADIUS =
            (altarPosWorld, radius) -> Utilities.isGroundUnderAltarSolid(altarPosWorld.getFirst(), altarPosWorld.getSecond(), radius).getFirst();


}
