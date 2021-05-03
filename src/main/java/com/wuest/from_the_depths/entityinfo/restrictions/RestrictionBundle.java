package com.wuest.from_the_depths.entityinfo.restrictions;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.base.Weather;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RestrictionBundle {

    public static final String[] OPERATORS = {"==", "!=", ">", "<", ">=", "<="};

    private Integer[] dimensions = null;

    private int timeOfDayOperator = -1;
    private Long timeOfDay = null;

    private Weather weather = null;

    private ResourceLocation[] biomes = null;

    private int yLevelOperator = -1;
    private Integer yLevel = null;

    private Integer groundRadius = null;

    public void add(SpawnRestrictionType type, Object data, int op)
    {
        switch (type) {
            case DIMENSION:
                dimensions = (Integer[]) data;
                break;
            case TIME_OF_DAY:
                timeOfDayOperator = op;
                timeOfDay = ((Long) data);
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
                yLevelOperator = op;
                yLevel = (Integer) data;
                break;
            case GROUND_RADIUS:
                groundRadius = (Integer) data;
                break;
        }
    }

    /**
     * operator > -> true when in-game value is higher than configured value
     * operator < -> true when in-game value is lower than configured value
     */
    public boolean testAll(World world, BlockPos pos) {
        boolean canStart = true;

        //It's only a bool assignment because it's the first condition and before the boolean base value is always true
        if (dimensions != null)
            canStart = SpawnRestrictions.DIMENSION.test(world, dimensions);

        if (timeOfDay != null) {
            if (timeOfDayOperator == 2)
                canStart &= SpawnRestrictions.TIME_OF_DAY_GREATER.test(world, timeOfDay);
            else if (timeOfDayOperator == 3)
                canStart &= SpawnRestrictions.TIME_OF_DAY_LESS.test(world, timeOfDay);
            else {
                canStart &= SpawnRestrictions.TIME_OF_DAY_LESS.test(world, timeOfDay);
                FromTheDepths.logger.error("Time of Day operator can only be < or >. (Defaulting to < ...)");
            }
        }

        if (weather != null)
            canStart &= SpawnRestrictions.WEATHER.test(world, weather);

        if (biomes != null)
            canStart &= SpawnRestrictions.BIOME.test(new Tuple<>(pos, world), biomes);

        if (yLevel != null) {
            switch (yLevelOperator) {
                case 0:
                    SpawnRestrictions.Y_LEVEL_EQUALS.test(pos, yLevel);
                    break;
                case 2:
                    SpawnRestrictions.Y_LEVEL_GREATER.test(pos, yLevel);
                    break;
                case 3:
                    SpawnRestrictions.Y_LEVEL_LESS.test(pos, yLevel);
                    break;
            }
        }

        if (groundRadius != null)
            SpawnRestrictions.GROUND_RADIUS.test(new Tuple<>(pos, world), groundRadius);

        return canStart;
    }
}
