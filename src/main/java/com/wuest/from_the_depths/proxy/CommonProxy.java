package com.wuest.from_the_depths.proxy;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.events.ModEventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is the server side proxy.
 * 
 * @author WuestMan
 *
 */
public class CommonProxy implements IGuiHandler {
  public static ModEventHandler eventHandler = new ModEventHandler();
  public static ModConfiguration proxyConfiguration;

  public Path modDirectory;

  /*
   * Methods for ClientProxy to Override
   */
  public void registerRenderers() {
  }

  public void preInit(FMLPreInitializationEvent event) {
    this.modDirectory = Paths.get(event.getModConfigurationDirectory().getAbsolutePath(), "FTD_Summons");

    Path subDir = modDirectory.resolve("textures");
    subDir.toFile().mkdirs();
    subDir.resolveSibling("models/item").toFile().mkdirs();

    ModRegistry.RegisterSpawningInfo();

    ModRegistry.registerSpawnRestrictions();

    ModRegistry.RegisterModComponents();

    FromTheDepths.network = NetworkRegistry.INSTANCE.newSimpleChannel("FTDChannel123");

    FromTheDepths.config = new Configuration(event.getSuggestedConfigurationFile());

    FromTheDepths.config.load();
    ModConfiguration.syncConfig();

    // Register messages.
    ModRegistry.RegisterMessages();

    // Register the capabilities.
    ModRegistry.RegisterCapabilities();

    // Make sure that the mod configuration is re-synced after loading all
    // of the recipes.
    ModConfiguration.syncConfig();
  }

  public void init(FMLInitializationEvent event) {
    NetworkRegistry.INSTANCE.registerGuiHandler(FromTheDepths.instance, FromTheDepths.proxy);

    // Register custom recipes here.
    ModRegistry.RegisterTotemOfSummoningRecipes();
  }

  public void postinit(FMLPostInitializationEvent event) {
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return ModRegistry.GetModGuiByID(ID, x, y, z);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return ModRegistry.GetModGuiByID(ID, x, y, z);
  }

  public ModConfiguration getServerConfiguration() {
    return CommonProxy.proxyConfiguration;
  }
}
