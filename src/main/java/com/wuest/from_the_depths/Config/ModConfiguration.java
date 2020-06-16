package com.wuest.from_the_depths.Config;

import java.util.HashMap;
import java.util.Map.Entry;

import com.wuest.from_the_depths.FromTheDepths;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is used to hold the mod configuration.
 * 
 * @author WuestMan
 *
 */
public class ModConfiguration {
  public static String OPTIONS = "general.options";
  public static String RecipeOptions = "general.options.recipes";
  public static String tagKey = "FromTheDepthsConfig";

  // Config file option names.
  private static String allowAltarToBeDestroyedName = "Allow Altar to be Destroyed";

  // Configuration Options.
  public boolean enableStructurePreview;
  public boolean allowAltarToBeDestroyed;

  public HashMap<String, Boolean> recipeConfiguration;

  // Recipe Options
  public static String arenaStructureKey = "Arena";

  public static String[] recipeKeys = new String[] { arenaStructureKey, };

  public ModConfiguration() {
    this.recipeConfiguration = new HashMap<String, Boolean>();
  }

  public static void syncConfig() {
    Configuration config = FromTheDepths.config;

    if (FromTheDepths.proxy.proxyConfiguration == null) {
      FromTheDepths.proxy.proxyConfiguration = new ModConfiguration();
    }

    // General settings.

    FromTheDepths.proxy.proxyConfiguration.allowAltarToBeDestroyed = config.getBoolean(
        ModConfiguration.allowAltarToBeDestroyedName, ModConfiguration.OPTIONS, false,
        "Determines if the Altar can be destroyed. server configuration overrides client.");

    // Recipe configuration.
    for (String key : ModConfiguration.recipeKeys) {
      boolean value = config.getBoolean(key, RecipeOptions, true,
          "Determines if the recipe(s) associated with the " + key + " are enabled.");
      FromTheDepths.proxy.proxyConfiguration.recipeConfiguration.put(key, value);
    }

    if (config.hasChanged()) {
      config.save();
    }
  }

  public NBTTagCompound ToNBTTagCompound() {
    NBTTagCompound tag = new NBTTagCompound();

    tag.setBoolean(ModConfiguration.allowAltarToBeDestroyedName, this.allowAltarToBeDestroyed);

    for (Entry<String, Boolean> entry : this.recipeConfiguration.entrySet()) {
      tag.setBoolean(entry.getKey(), entry.getValue());
    }

    return tag;
  }

  public static ModConfiguration getFromNBTTagCompound(NBTTagCompound tag) {
    ModConfiguration config = new ModConfiguration();

    config.allowAltarToBeDestroyed = tag.getBoolean(ModConfiguration.allowAltarToBeDestroyedName);

    for (String key : ModConfiguration.recipeKeys) {
      config.recipeConfiguration.put(key, tag.getBoolean(key));
    }

    return config;
  }
}
