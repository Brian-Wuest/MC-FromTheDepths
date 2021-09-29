package com.wuest.from_the_depths.entityinfo.restrictions;

import com.wuest.from_the_depths.base.Weather;
//import com.wuest.from_the_depths.integration.SereneSeasonHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

/**
 * Bundles all restriction types together.
 */
@SuppressWarnings("FieldMayBeFinal")
public class RestrictionBundle {

    private Integer[] dimensions;

    private DataAndComparator<Long> timeOfDay;

    private Weather weather;

    private ResourceLocation[] biomes;

    private DataAndComparator<Integer> yLevel;

    private int groundRadius;

    public RestrictionBundle()
    {
        this.dimensions = null;
        this.timeOfDay = null;
        this.weather = null;
        this.biomes = null;
        this.yLevel = null;
        groundRadius = 0;
    }

    public Pair<Boolean, TextComponentTranslation> testAll(String spawnKey, World world, BlockPos pos) {
        boolean canStart = true;
        TextComponentTranslation message = null;

        if (this.dimensions != null) {
            canStart = SpawnRestrictions.DIMENSION.test(world, this.dimensions);

            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.dimension");
            }
        }

        if (canStart && this.timeOfDay != null) {
            canStart = SpawnRestrictions.TIME_OF_DAY.test(world, this.timeOfDay);
            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.time_of_day");
            }
        }

        if (canStart && this.weather != null) {
            canStart = SpawnRestrictions.WEATHER.test(world, this.weather);

            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.weather", weather.name().toLowerCase());
            }
        }

        if (canStart && this.biomes != null) {
            canStart = SpawnRestrictions.BIOME.test(new Tuple<>(pos, world), this.biomes);

            if (!canStart) {
                String biomesString = Arrays.stream(this.biomes).map(resLoc -> resLoc.getPath().replace("_", "")).reduce("", (s, s2) -> s + ", " + s2);
                message = new TextComponentTranslation("from_the_depths.restrictions.biomes", biomesString);
            }
        }

        if (canStart && this.yLevel != null) {
            canStart = SpawnRestrictions.Y_LEVEL.test(pos, this.yLevel);

            if (!canStart) {
                String key = "from_the_depths.restrictions.y_level_";

                if (this.yLevel.operator == DataAndComparator.Operator.MORE) {
                    key += "higher";
                }
                else if (this.yLevel.operator == DataAndComparator.Operator.LESS) {
                    key += "lower";
                }
                else {
                    key += "equals";
                }

                message = new TextComponentTranslation(key, this.yLevel.data);
            }
        }

        if (canStart && this.groundRadius != 0) {
            canStart = SpawnRestrictions.GROUND_RADIUS.test(new Tuple<>(pos, world), this.groundRadius);

            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.ground_radius", groundRadius);
            }
        }

        return Pair.of(canStart, message);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("RestrictionBundle: ");

        if (this.dimensions != null) {
            builder.append("dimensions=").append(Arrays.toString(this.dimensions));
        }

        if (this.timeOfDay != null) {
            builder.append("| timeOfDay=").append(this.timeOfDay);
        }

        if (this.weather != null) {
            builder.append("| weather=").append(this.weather);
        }

        if (this.biomes != null) {
            builder.append("| biomes=").append(Arrays.toString(this.biomes));
        }

        if (this.yLevel != null) {
            builder.append("| yLevel=").append(this.yLevel);
        }

        if (this.groundRadius != 0) {
            builder.append("| groundRadius=").append(this.groundRadius);
        }

        return builder.toString();
    }
}
