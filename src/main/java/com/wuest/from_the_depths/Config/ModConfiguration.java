package com.wuest.from_the_depths.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.UpdateChecker;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import scala.Tuple2;

/**
 * This class is used to hold the mod configuration.
 * @author WuestMan
 *
 */
public class ModConfiguration
{
	public static String OPTIONS = "general.options";
	public static String RecipeOptions = "general.options.recipes";
	public static String tagKey = "FromTheDepthsConfig";

	// Config file option names.
	private static String showMessageName = "Show Message";
	private static String enableStructurePreviewName = "Include Structure Previews";

	// Configuration Options.
	public boolean enableStructurePreview;

	public HashMap<String, Boolean> recipeConfiguration;

	// Recipe Options
	public static String arenaStructureKey = "Arena";

	public static String[] recipeKeys = new String[] 
	{ 
		arenaStructureKey,
	};
	
	public ModConfiguration()
	{
		this.recipeConfiguration = new HashMap<String, Boolean>();
	}

	public static void syncConfig()
	{
		Configuration config = FromTheDepths.config;

		if (FromTheDepths.proxy.proxyConfiguration == null)
		{
			FromTheDepths.proxy.proxyConfiguration = new ModConfiguration();
		}

		// General settings.
		FromTheDepths.proxy.proxyConfiguration.enableStructurePreview = config.getBoolean(ModConfiguration.enableStructurePreviewName, ModConfiguration.OPTIONS, true, "Determines if the Preview buttons in structure GUIs and other structure previews functions are enabled. Client side only.");
		
		// Recipe configuration.
		for (String key : ModConfiguration.recipeKeys)
		{
			boolean value = config.getBoolean(key, RecipeOptions, true, "Determines if the recipe(s) associated with the " + key + " are enabled.");
			FromTheDepths.proxy.proxyConfiguration.recipeConfiguration.put(key, value);
		}

		if (config.hasChanged()) 
		{
			config.save();
		}
	}

	public NBTTagCompound ToNBTTagCompound()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setBoolean(ModConfiguration.enableStructurePreviewName, this.enableStructurePreview);
		tag.setBoolean(ModConfiguration.showMessageName, UpdateChecker.showMessage);

		for (Entry<String, Boolean> entry : this.recipeConfiguration.entrySet())
		{
			tag.setBoolean(entry.getKey(), entry.getValue());
		}
		
		if (ModRegistry.TotemOfSpawning().subItems.size() > 0)
		{
			NBTTagCompound totems = new NBTTagCompound();
			
			for (ItemStack stack : ModRegistry.TotemOfSpawning().subItems)
			{
				ResourceLocation location = ModRegistry.TotemOfSpawning().getEntityResourceNameFromItemStack(stack);
				
				if (location != null)
				{
					totems.setString(location.toString(), location.toString());
				}
			}
			
			tag.setTag("totems", totems);
		}

		return tag;
	}

	public static ModConfiguration getFromNBTTagCompound(NBTTagCompound tag)
	{
		ModConfiguration config = new ModConfiguration();

		config.enableStructurePreview = tag.getBoolean(ModConfiguration.enableStructurePreviewName);
		UpdateChecker.showMessage = tag.getBoolean(ModConfiguration.showMessageName);

		for (String key : ModConfiguration.recipeKeys)
		{
			config.recipeConfiguration.put(key, tag.getBoolean(key));
		}
		
		if (tag.hasKey("totems"))
		{
			NBTTagCompound totems = tag.getCompoundTag("totems");
			
			for (String key : totems.getKeySet())
			{
				ResourceLocation totemEntity = new ResourceLocation(totems.getString(key));
				ItemStack stack = ModRegistry.TotemOfSpawning().getItemStackUsingEntityResourceName(totemEntity);
				
				if (!stack.isEmpty())
				{
					ModRegistry.TotemOfSpawning().serverSubItems.add(stack);
				}
			}
		}

		return config;
	}
}
