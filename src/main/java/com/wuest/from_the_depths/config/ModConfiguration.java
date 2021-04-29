package com.wuest.from_the_depths.config;

import java.util.HashMap;
import java.util.Map.Entry;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.proxy.CommonProxy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is used to hold the mod configuration.
 *
 * @author WuestMan
 */
public class ModConfiguration {
	public static String OPTIONS = "general.options";
	public static String RecipeOptions = "general.options.recipes";
	public static String tagKey = "FromTheDepthsConfig";

	// Config file option names.
	private static String allowAltarToBeDestroyedName = "Allow Altar to be Destroyed";
	private static String altarSpawningRadiusName = "Altar Spawning Radius";
	private static String altarSpawningHeightName = "Altar Spawning Height";
	private static String showAltarSpawningTextName = "Show Altar Spawning Text";
	private static String enableArenaStyleRestrictionsName = "Enable Arena Style Restrictions";

	// Configuration Options.
	public boolean allowAltarToBeDestroyed;
	public int altarSpawningRadius;
	public int altarSpawningHeight;
	public boolean showAltarSpawningText;
	public boolean enableArenaStyleRestrictions;

	public HashMap<String, Boolean> recipeConfiguration;

	// Recipe Options
	public static String arenaStructureKey = "Arena";

	public static String[] recipeKeys = new String[]{arenaStructureKey,};

	public ModConfiguration() {
		this.recipeConfiguration = new HashMap<String, Boolean>();
	}

	public static void syncConfig() {
		Configuration config = FromTheDepths.config;

		if (CommonProxy.proxyConfiguration == null) {
			CommonProxy.proxyConfiguration = new ModConfiguration();
		}

		// General settings.

		CommonProxy.proxyConfiguration.allowAltarToBeDestroyed = config.getBoolean(
				ModConfiguration.allowAltarToBeDestroyedName, ModConfiguration.OPTIONS, false,
				"Determines if the Altar can be destroyed. server configuration overrides client.");

		CommonProxy.proxyConfiguration.altarSpawningRadius = config.getInt(
				ModConfiguration.altarSpawningRadiusName, ModConfiguration.OPTIONS, 6, 6, 32,
				"The number of blocks around an altar of spawning where monsters can spawn. server configuration overrides client.");

		CommonProxy.proxyConfiguration.altarSpawningHeight = config.getInt(
				ModConfiguration.altarSpawningHeightName, ModConfiguration.OPTIONS, 6, 6, 16,
				"The number of blocks above an altar of spawning where flying monsters can spawn. server configuration overrides client.");

		CommonProxy.proxyConfiguration.showAltarSpawningText = config.getBoolean(
				ModConfiguration.showAltarSpawningTextName, ModConfiguration.OPTIONS, true,
				"Determines if text is shown above the altar of spawning when the altar is processing a totem. server configuration overrides client.");

		CommonProxy.proxyConfiguration.enableArenaStyleRestrictions = config.getBoolean(
				ModConfiguration.enableArenaStyleRestrictionsName, ModConfiguration.OPTIONS, true,
				"Determines if the area around the altar needs to be solid and flat. server configuration overrides client.");

		// Recipe configuration.
		for (String key : ModConfiguration.recipeKeys) {
			boolean value = config.getBoolean(key, RecipeOptions, true,
					"Determines if the recipe(s) associated with the " + key + " are enabled.");
			CommonProxy.proxyConfiguration.recipeConfiguration.put(key, value);
		}

		if (config.hasChanged()) {
			config.save();
		}
	}

	public NBTTagCompound ToNBTTagCompound() {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setBoolean(ModConfiguration.allowAltarToBeDestroyedName, this.allowAltarToBeDestroyed);
		tag.setInteger(ModConfiguration.altarSpawningRadiusName, this.altarSpawningRadius);
		tag.setInteger(ModConfiguration.altarSpawningHeightName, this.altarSpawningHeight);
		tag.setBoolean(ModConfiguration.showAltarSpawningTextName, this.showAltarSpawningText);

		for (Entry<String, Boolean> entry : this.recipeConfiguration.entrySet()) {
			tag.setBoolean(entry.getKey(), entry.getValue());
		}

		return tag;
	}

	public static ModConfiguration getFromNBTTagCompound(NBTTagCompound tag) {
		ModConfiguration config = new ModConfiguration();

		config.allowAltarToBeDestroyed = tag.getBoolean(ModConfiguration.allowAltarToBeDestroyedName);
		config.altarSpawningHeight = tag.getInteger(ModConfiguration.altarSpawningHeightName);
		config.altarSpawningRadius = tag.getInteger(ModConfiguration.altarSpawningRadiusName);
		config.showAltarSpawningText = tag.getBoolean(ModConfiguration.showAltarSpawningTextName);

		for (String key : ModConfiguration.recipeKeys) {
			config.recipeConfiguration.put(key, tag.getBoolean(key));
		}

		return config;
	}
}
