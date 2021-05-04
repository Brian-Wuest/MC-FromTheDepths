package com.wuest.from_the_depths.entityinfo.restrictions;

import com.wuest.from_the_depths.base.Weather;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

public class RestrictionBundle {

    private Integer[] dimensions;

    private DataAndComparator<Long> timeOfDay;

    private Weather weather;

    private ResourceLocation[] biomes;

    private DataAndComparator<Integer> yLevel;

    private int groundRadius;

    public RestrictionBundle()
    {
        dimensions = null;
        timeOfDay = null;
        weather = null;
        biomes = null;
        groundRadius = 0;
    }

    public void add(SpawnRestrictionType type, Object data, String op)
    {
        switch (type) {
            case DIMENSION:
                dimensions = (Integer[]) data;
                break;
            case TIME_OF_DAY:
                timeOfDay = new DataAndComparator<>((Long) data, DataAndComparator.Operator.valueOf(op));
                break;
            case WEATHER:
                weather = Weather.valueOf(((String) data).toUpperCase());
                break;
            case BIOME:
                ResourceLocation[] biomeRess = new ResourceLocation[((String[]) data).length];
                for (int i = 0; i < ((String[]) data).length; i++)
                    biomeRess[i] = new ResourceLocation(((String[]) data)[i]);
                biomes = biomeRess;
                break;
            case Y_LEVEL:
                yLevel = new DataAndComparator<>((Integer) data, DataAndComparator.Operator.valueOf(op));
                break;
            case GROUND_RADIUS:
                groundRadius = (Integer) data;
                break;
        }
    }

    public Tuple<Boolean, TextComponentTranslation> testAll(World world, BlockPos pos) {
        boolean canStart = true;
        TextComponentTranslation message = null;

        //It's only a bool assignment because it's the first condition and before the boolean base value is always true
        if (dimensions != null) {
            canStart = SpawnRestrictions.DIMENSION.test(world, dimensions);

            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.dimension");
            }
        }

        if (canStart && timeOfDay != null) {
            canStart = SpawnRestrictions.TIME_OF_DAY.test(world, timeOfDay);
            if (!canStart)
                message = new TextComponentTranslation("from_the_depths.restrictions.time_of_day");
        }

        if (canStart && weather != null) {
            canStart = SpawnRestrictions.WEATHER.test(world, weather);

            if (!canStart)
                message = new TextComponentTranslation("from_the_depths.restrictions.weather", weather.name().toLowerCase());
        }

        if (canStart && biomes != null) {
            canStart = SpawnRestrictions.BIOME.test(new Tuple<>(pos, world), biomes);
            if (!canStart) {
                String biomesString = Arrays.stream(biomes).map(resLoc -> resLoc.getResourcePath().replace("_", "")).reduce("", (s, s2) -> s + ", " + s2);
                message = new TextComponentTranslation("from_the_depths.restrictions.biomes", biomesString);
            }
        }

        if (canStart && yLevel != null) {
            canStart = SpawnRestrictions.Y_LEVEL.test(pos, yLevel);

            if (!canStart) {
                String key = "from_the_depths.restrictions.y_level_";
                if (yLevel.operator == DataAndComparator.Operator.MORE)
                    key += "higher";
                else if (yLevel.operator == DataAndComparator.Operator.LESS)
                    key += "lower";
                else key += "equals";

                message = new TextComponentTranslation(key, yLevel.data);
            }
        }

        if (canStart && groundRadius != 0) {
            canStart = SpawnRestrictions.GROUND_RADIUS.test(new Tuple<>(pos, world), groundRadius);
            if (!canStart)
                message = new TextComponentTranslation("from_the_depths.restrictions.ground_radius", groundRadius);
        }

        return new Tuple<>(canStart, message);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("RestrictionBundle: ");

        if (dimensions != null)
            builder.append("dimensions=").append(Arrays.toString(dimensions));
        if (timeOfDay != null)
            builder.append("| timeOfDay=").append(timeOfDay);
        if (weather != null)
            builder.append("| weather=").append(weather);
        if (biomes != null)
            builder.append("| biomes=").append(Arrays.toString(biomes));
        if (yLevel != null)
            builder.append("| yLevel=").append(yLevel);
        if (groundRadius != 0)
            builder.append("| groundRadius=").append(groundRadius);

        return builder.toString();
    }
}
