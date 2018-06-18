package com.wuest.from_the_depths.Proxy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.filechooser.FileFilter;

import com.google.common.io.Files;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.UpdateChecker;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Events.ModEventHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * This is the server side proxy.
 * 
 * @author WuestMan
 *
 */
public class CommonProxy implements IGuiHandler
{
	public static ModEventHandler eventHandler = new ModEventHandler();
	public static ModConfiguration proxyConfiguration;

	public Path modDirectory;
	public File spawnInfoFile;
	public Path spawnInfoFilePath;

	/*
	 * Methods for ClientProxy to Override
	 */
	public void registerRenderers()
	{
	}

	public void preInit(FMLPreInitializationEvent event)
	{
		this.modDirectory = Paths.get(
				event.getModConfigurationDirectory().getAbsolutePath(),
				"FTD_Summons");
		
		for (File file : this.modDirectory.toFile().listFiles())
		{
			if (file.isFile())
			{
				Path path = file.toPath();
				String name = Files.getNameWithoutExtension(file.getName()).toLowerCase();
				
				if (name.contains("spawninfo"))
				{
					this.spawnInfoFilePath = path;
					this.spawnInfoFile = file;
					break;
				}
			}
		}

		FromTheDepths.network = NetworkRegistry.INSTANCE
				.newSimpleChannel("FTDChannel123");
		
		FromTheDepths.config = new Configuration(
				event.getSuggestedConfigurationFile());
		
		FromTheDepths.config.load();
		ModConfiguration.syncConfig();

		// Register messages.
		ModRegistry.RegisterMessages();

		// Register the capabilities.
		ModRegistry.RegisterCapabilities();

		// Make sure that the mod configuration is re-synced after loading all
		// of the recipes.
		ModConfiguration.syncConfig();

		if (!this.modDirectory.toFile().exists())
		{
			FromTheDepths.logger.warn(
					"From_The_Depths: The summons directory doesn't exist, unable to load boss summons. Please create this directory. Expected directory: {}",
					this.modDirectory.toString());
		}

	}

	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(FromTheDepths.instance,
				FromTheDepths.proxy);

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
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z)
	{
		return ModRegistry.GetModGuiByID(ID, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z)
	{
		return ModRegistry.GetModGuiByID(ID, x, y, z);
	}

	public ModConfiguration getServerConfiguration()
	{
		return CommonProxy.proxyConfiguration;
	}
}
