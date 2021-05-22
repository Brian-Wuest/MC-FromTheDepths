package com.wuest.from_the_depths.entityinfo.restrictions;

import com.wuest.from_the_depths.Utilities;
import com.wuest.from_the_depths.base.Weather;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.BiPredicate;

/**
 * @author Davoleo
 */
public class SpawnRestrictions {

    //Current Dimension
    public static final BiPredicate<World, Integer[]> DIMENSION = (world, dimensions) -> ArrayUtils.contains(dimensions, world.provider.getDimension());

    //World Time of Day
    public static final BiPredicate<World, DataAndComparator<Long>> TIME_OF_DAY = (world, time) -> time.operator.test(world.provider.getWorldTime(), time.data);

    //Weather
    public static final BiPredicate<World, Weather> WEATHER = (world, weather) -> weather.isCurrentState(world.getWorldInfo());

    //Biome
    public static final BiPredicate<Tuple<BlockPos, World>, ResourceLocation[]> BIOME = (blockPosWorldPair, biomes) -> {
        Biome biome = blockPosWorldPair.getSecond().provider.getBiomeForCoords(blockPosWorldPair.getFirst());
        return ArrayUtils.contains(biomes, biome.getRegistryName());
    };

    // Y Level
    public static final BiPredicate<BlockPos, DataAndComparator<Integer>> Y_LEVEL = (blockPos, yLevel) -> yLevel.operator.test(blockPos.getY(), yLevel.data);

    // Ground Radius
    public static final BiPredicate<Tuple<BlockPos, World>, Integer> GROUND_RADIUS =
            (altarPosWorld, radius) -> Utilities.isGroundUnderAltarSolid(altarPosWorld.getFirst(), altarPosWorld.getSecond(), radius).getFirst();

}
