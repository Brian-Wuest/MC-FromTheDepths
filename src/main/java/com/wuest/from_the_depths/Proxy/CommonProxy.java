package com.wuest.from_the_depths.Proxy;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.io.Files;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.UpdateChecker;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Events.ModEventHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This is the server side proxy.
 * @author WuestMan
 *
 */
public class CommonProxy implements IGuiHandler
{
	public static ModEventHandler eventHandler = new ModEventHandler();
	public static ModConfiguration proxyConfiguration;
	
	public File modDirectory;
	public File spawnInfoFile;

	/*
	 * Methods for ClientProxy to Override
	 */
	public void registerRenderers()
	{
	}
	
	public void preInit(FMLPreInitializationEvent event)
	{
		this.modDirectory = new File(event.getModConfigurationDirectory().getAbsolutePath() + "\\FTD_Summons");
		this.spawnInfoFile = new File(event.getModConfigurationDirectory().getAbsolutePath() + "\\FTD_Summons\\spawnInfo.json");
		
		FromTheDepths.network = NetworkRegistry.INSTANCE.newSimpleChannel("FTDChannel123");
		FromTheDepths.config = new Configuration(event.getSuggestedConfigurationFile());
		FromTheDepths.config.load();
		ModConfiguration.syncConfig();
		
		// Register messages.
		ModRegistry.RegisterMessages();
		
		// Register the capabilities.
		ModRegistry.RegisterCapabilities();
		
		// Make sure that the mod configuration is re-synced after loading all of the recipes.
		ModConfiguration.syncConfig();
		
		if (!this.modDirectory.exists())
		{
			try
			{
				java.nio.file.Files.createDirectory(this.modDirectory.toPath());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void init(FMLInitializationEvent event)
	{	
		NetworkRegistry.INSTANCE.registerGuiHandler(FromTheDepths.instance, FromTheDepths.proxy);
		
		// Register the spawning info here.
		ModRegistry.RegisterSpawningInfo();
		
		// Register custom recipes here.
		ModRegistry.RegisterTotemOfSummoningRecipes();
	}
	
	public void postinit(FMLPostInitializationEvent event)
	{
		if (UpdateChecker.showMessage)
		{
			UpdateChecker.checkVersion();
		}
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return ModRegistry.GetModGuiByID(ID, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return ModRegistry.GetModGuiByID(ID, x, y, z);
	}

	public ModConfiguration getServerConfiguration()
	{
		return CommonProxy.proxyConfiguration;
	}
}
