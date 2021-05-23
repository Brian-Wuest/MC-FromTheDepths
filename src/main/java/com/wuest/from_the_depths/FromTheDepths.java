package com.wuest.from_the_depths;

import com.wuest.from_the_depths.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(modid = FromTheDepths.MODID, version = FromTheDepths.VERSION, acceptedMinecraftVersions = "[1.12.2]", guiFactory = "com.wuest.from_the_depths.gui.ConfigGuiFactory", updateJSON = "https://raw.githubusercontent.com/Brian-Wuest/MC-FromTheDepths/master/changeLog.json")
public class FromTheDepths {
  public static final String MODID = "from_the_depths";
  public static final String VERSION = "@VERSION@";

  /**
   * This is used to determine if the mod is currently being debugged.
   */
  public static boolean isDebug = false;

  /**
   * This is the static instance of this class.
   */
  @Instance(value = FromTheDepths.MODID)
  public static FromTheDepths instance;

  /**
   * Says where the client and server 'proxy' code is loaded.
   */
  @SidedProxy(clientSide = "com.wuest.from_the_depths.proxy.ClientProxy", serverSide = "com.wuest.from_the_depths.proxy.CommonProxy")
  public static CommonProxy proxy;

  /**
   * The network class used to send messages.
   */
  public static SimpleNetworkWrapper network;

  /**
   * This is the configuration of the mod.
   */
  public static Configuration config;

  public static Logger logger;

  public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("fromTheDepths") {
    @Nonnull
    @Override
    public ItemStack createIcon()
    {
      return new ItemStack(ModRegistry.AlterOfSpawning());
    }
  };

  static {
    FromTheDepths.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
        .contains("-agentlib:jdwp");
  }

  /**
   * The pre-initialization event.
   * @param event The event from forge.
   */
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    FromTheDepths.logger = event.getModLog();
    FromTheDepths.proxy.preInit(event);
  }

  /**
   * The initialization event.
   * 
   * @param event The event from forge.
   */
  @EventHandler
  public void init(FMLInitializationEvent event) {
    FromTheDepths.proxy.init(event);
  }

  /**
   * The post-initialization event.
   * 
   * @param event The event from forge.
   */
  @EventHandler
  public void postinit(FMLPostInitializationEvent event) {
    FromTheDepths.proxy.postinit(event);
  }
}