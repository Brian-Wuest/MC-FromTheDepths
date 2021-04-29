package com.wuest.from_the_depths.proxy;

import com.wuest.from_the_depths.ClientModRegistry;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.events.ClientEventHandler;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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