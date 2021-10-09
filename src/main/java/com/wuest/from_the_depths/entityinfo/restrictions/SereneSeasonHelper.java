package com.wuest.from_the_depths.entityinfo.restrictions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

/**
 * Helper class that contains Utility Methods to interact with Serene Seasons.
 * (trying to keep the mod as an Optional Soft Dependency)
 */
public class SereneSeasonHelper {

    public static final BooleanSupplier isSereneSeasonLoaded = () -> Loader.isModLoaded("sereneseasons");

    private static final BiPredicate<Pair<Season, Season.SubSeason>, World> SEASON_SPAWN_RESTRICTION = (season, world) -> {
        ISeasonState worldSeason = SeasonHelper.getSeasonState(world);
        if (season.getRight() == null) {
            return worldSeason.getSeason() == season.getLeft();
        } else {
            return worldSeason.getSeason() == season.getLeft() && worldSeason.getSubSeason() == season.getRight();
        }
    };

    public static Map<String, Pair<Season, Season.SubSeason>> seasonRestrictions = new HashMap<>();

    /**
     * Parses {@link Season} and {@link sereneseasons.api.season.Season.SubSeason} objects from string parameters and returns a pair of them
     *
     * @param seasonStr    Season string representation (should be the same name as {@link Season} constants)
     * @param subSeasonStr SubSeason string representation (should be the same name as {@link sereneseasons.api.season.Season.SubSeason} constants)
     * @throws IllegalArgumentException When any of the string representation of enum constants are wrong (the exception IS NOT handled here)
     */
    private static Pair<Season, Season.SubSeason> seasonFromStrings(String seasonStr, String subSeasonStr) throws IllegalArgumentException {

        Season season = Season.valueOf(seasonStr);

        Season.SubSeason subSeason = null;
        if (!subSeasonStr.equals("")) {
            subSeason = Season.SubSeason.valueOf(subSeasonStr + '_' + seasonStr);
        }

        return Pair.of(season, subSeason);
    }

    /**
     * Adds a season restriction in the form of a Pair of seasons to the {@link SereneSeasonHelper#seasonRestrictions} map
     *
     * @param spawnKey  The boss key this season restriction is bound to.
     * @param seasonObj The season restriction information (directly from the JSON)
     * @throws JsonSyntaxException when the "season" attribute is not present
     *                             (Note that subSeason is optional and fallbacks to "" in case it's not given any value)
     */
    public static void addSeasonRestriction(String spawnKey, JsonObject seasonObj) throws JsonSyntaxException {
        String sString = JsonUtils.getString(seasonObj, "season");
        String sSString = JsonUtils.getString(seasonObj, "subSeason", "");
        Pair<Season, Season.SubSeason> seasons = seasonFromStrings(sString.toUpperCase(), sSString.toUpperCase());
        SereneSeasonHelper.seasonRestrictions.put(spawnKey, seasons);
    }

    /**
     * @param world The world
     * @return whether season restrictions are asserted for the passed boss spawn key.
     */
    public static Pair<Boolean, String> testSeasonRestrictions(String spawnKey, World world) {
        // Return true if Serene Seasons is not installed
        if (!SereneSeasonHelper.isSereneSeasonLoaded.getAsBoolean()) {
            return Pair.of(true, null);
        }

        Pair<Season, Season.SubSeason> seasonPair = SereneSeasonHelper.seasonRestrictions.get(spawnKey);

        // Return true if no season restrictions are specified
        if (seasonPair == null) {
            return Pair.of(true, null);
        }

        String correctSeason;

        // Get the correct season
        if (seasonPair.getRight() != null) {
            correctSeason = seasonPair.getRight().toString().replace('_', ' ').toLowerCase();
        } else {
            correctSeason = seasonPair.getLeft().toString().toLowerCase();
        }

        // Test the SEASON predicate
        boolean result = SereneSeasonHelper.SEASON_SPAWN_RESTRICTION.test(SereneSeasonHelper.seasonRestrictions.get(spawnKey), world);

        return Pair.of(result, correctSeason);
    }

    @SideOnly(Side.CLIENT)
    public static void addSeasonRestrictionToToolTip(List<String> toolTip, String spawnKey) {
        if (SereneSeasonHelper.seasonRestrictions.containsKey(spawnKey)) {
            Pair<Season, Season.SubSeason> seasonPair = SereneSeasonHelper.seasonRestrictions.get(spawnKey);
            String correctSeason;

            if (seasonPair.getRight() != null) {
                correctSeason = seasonPair.getRight().toString().replace('_', ' ').toLowerCase();
            } else {
                correctSeason = seasonPair.getLeft().toString().toLowerCase();
            }

            toolTip.add(TextFormatting.BLUE + "Required Season" + TextFormatting.WHITE + ": " + correctSeason);
        }
    }
}
