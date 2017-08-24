package com.wuest.from_the_depths.Proxy;

import com.wuest.from_the_depths.*;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Events.ClientEventHandler;
import com.wuest.from_the_depths.Render.ShaderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;

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
		ShaderHelper.Initialize();
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