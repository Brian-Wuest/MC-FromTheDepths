package com.wuest.from_the_depths.proxy;

import com.wuest.from_the_depths.ClientModRegistry;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.davoleo.TotemTextureLoader;
import com.wuest.from_the_depths.events.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.List;

/**
 * 
 * @author WuestMan
 *
 */
public class ClientProxy extends CommonProxy
{
	public ModConfiguration serverConfiguration = null;
	public static ClientEventHandler clientEventHandler = new ClientEventHandler();
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);

		ModRegistry.AddGuis();
		
		// After all items have been registered and all recipes loaded, register any necessary renderer.
		FromTheDepths.proxy.registerRenderers();

		//Add Totem Textures Loader
		List<IResourcePack> defaultReosurcePacks =
				ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_110449_ao");
		TotemTextureLoader totemTextureLoader = new TotemTextureLoader();
		defaultReosurcePacks.add(totemTextureLoader);
		//Refreshes Totem textures since the first refresh pass called by forge happens before preInit
		FMLClientHandler.instance().refreshResources(totemTextureLoader);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}

	@Override
	public void postinit(FMLPostInitializationEvent event)
	{
		super.postinit(event);
	}
	
	@Override
	public void registerRenderers() 
	{
		ClientModRegistry.RegisterSpecialRenderers();
	}
	
	@Override
	public ModConfiguration getServerConfiguration()
	{
		if (this.serverConfiguration == null)
		{
			// Get the server configuration.
			return CommonProxy.proxyConfiguration;
		}
		else
		{
			return this.serverConfiguration;
		}
	}
}