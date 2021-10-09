package com.wuest.from_the_depths.entityinfo.restrictions;

import com.google.gson.annotations.SerializedName;
import com.wuest.from_the_depths.base.Weather;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Bundles all restriction types together.
 */
@SuppressWarnings("FieldMayBeFinal")
public class RestrictionBundle {

    @SerializedName(value = "dimensions", alternate = {"Dimensions"})
    private Integer[] dimensions;

    @SerializedName(value = "timeOfDay", alternate = {"TimeOfDay", "Timeofday", "TimeofDay", "time_of_day", "Time_Of_Day", "Time_of_Day", "Time_of_day"})
    private DataAndComparator<Long> timeOfDay;

    @SerializedName(value = "weather", alternate = {"Weather"})
    private Weather weather;

    @SerializedName(value = "biomes", alternate = {"Biomes"})
    private ResourceLocation[] biomes;

    @SerializedName(value = "yLevel", alternate = {"ylevel", "YLevel", "y_level"})
    private DataAndComparator<Integer> yLevel;

    private int groundRadius;

    public RestrictionBundle() {
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
                } else if (this.yLevel.operator == DataAndComparator.Operator.LESS) {
                    key += "lower";
                } else {
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

        if (canStart) {
            Pair<Boolean, String> resultAndCorrectSeason = SereneSeasonHelper.testSeasonRestrictions(spawnKey, world);
            canStart = resultAndCorrectSeason.getLeft();
            String correctSeason = resultAndCorrectSeason.getRight();
            if (!canStart) {
                message = new TextComponentTranslation("from_the_depths.restrictions.season", correctSeason);
            }
        }

        return Pair.of(canStart, message);
    }

    @SideOnly(Side.CLIENT)
    public void addToolTipInfo(List<String> toolTip, String spawnKey) {
        World world = Minecraft.getMinecraft().world;

        if (this.dimensions != null) {
            StringBuilder dimensionMessage = new StringBuilder(TextFormatting.BLUE + "Dimensions" + TextFormatting.WHITE + ": ");
            boolean addedOtherDimensions = false;

            for (int dimensionKey : this.dimensions) {
                if (DimensionManager.isDimensionRegistered(dimensionKey)) {
                    if (addedOtherDimensions) {
                        dimensionMessage.append(", ");
                    }

                    DimensionType dimensionType = DimensionManager.getProviderType(dimensionKey);
                    dimensionMessage.append(I18n.format(dimensionType.getName()));
                    addedOtherDimensions = true;
                }
            }

            toolTip.add(dimensionMessage.toString());
        }

        if (this.timeOfDay != null) {
            StringBuilder timeOfDayMessage = new StringBuilder(TextFormatting.BLUE + "Time of Day")
                    .append(TextFormatting.WHITE)
                    .append(": ");

            switch (this.timeOfDay.operator) {
                case EQUALS: {
                    timeOfDayMessage.append("At: ");
                    break;
                }

                case LESS: {
                    timeOfDayMessage.append("Before: ");
                    break;
                }

                case MORE: {
                    timeOfDayMessage.append("After: ");
                    break;
                }
            }

            timeOfDayMessage.append(this.timeOfDay.data);

            toolTip.add(timeOfDayMessage.toString());
        }

        if (this.weather != null) {
            String weatherMessage = TextFormatting.BLUE +
                    "Weather" +
                    TextFormatting.WHITE +
                    ": " +
                    this.weather.getName();

            toolTip.add(weatherMessage);
        }

        if (this.biomes != null) {
            StringBuilder biomeMessage = new StringBuilder(TextFormatting.BLUE + "Biomes" + TextFormatting.WHITE + ": ");
            boolean addedOtherBiome = false;

            for (ResourceLocation biomeRegistration : this.biomes) {
                if (ForgeRegistries.BIOMES.containsKey(biomeRegistration)) {
                    if (addedOtherBiome) {
                        biomeMessage.append(", ");
                    }

                    Biome biome = ForgeRegistries.BIOMES.getValue(biomeRegistration);
                    biomeMessage.append(I18n.format(biome.getBiomeName()));
                    addedOtherBiome = true;
                }
            }

            toolTip.add(biomeMessage.toString());
        }

        if (this.yLevel != null) {
            StringBuilder levelMessage = new StringBuilder(TextFormatting.BLUE + "Y-Level")
                    .append(TextFormatting.WHITE)
                    .append(": ");

            switch (this.yLevel.operator) {
                case EQUALS: {
                    levelMessage.append("At: ");
                    break;
                }

                case LESS: {
                    levelMessage.append("Below: ");
                    break;
                }

                case MORE: {
                    levelMessage.append("Above: ");
                    break;
                }
            }

            levelMessage.append(this.yLevel.data);

            toolTip.add(levelMessage.toString());
        }

        if (this.groundRadius != 0) {
            StringBuilder radiusMessage = new StringBuilder(TextFormatting.BLUE.toString())
                    .append("Solid Ground Around Altar")
                    .append(TextFormatting.WHITE)
                    .append(": ")
                    .append(this.groundRadius);

            toolTip.add(radiusMessage.toString());
        }

        if (SereneSeasonHelper.isSereneSeasonLoaded.getAsBoolean()) {
            SereneSeasonHelper.addSeasonRestrictionToToolTip(toolTip, spawnKey);
        }
    }

    @Override
    public String toString() {
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
