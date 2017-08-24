package com.wuest.from_the_depths.Proxy.Messages.Handlers;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.UpdateChecker;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Proxy.ClientProxy;
import com.wuest.from_the_depths.Proxy.Messages.ConfigSyncMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author WuestMan
 *
 */
public class ConfigSyncHandler implements
IMessageHandler<ConfigSyncMessage, IMessage>
{
	@Override
	public IMessage onMessage(final ConfigSyncMessage message,
			final MessageContext ctx) 
	{
		// Or Minecraft.getMinecraft() on the client.
		IThreadListener mainThread = null;
		
		if (ctx.side.isClient())
		{
			mainThread = Minecraft.getMinecraft();
		}
		else
		{
			mainThread = (WorldServer) ctx.getServerHandler().player.world;
		} 

		mainThread.addScheduledTask(new Runnable() 
		{
			@Override
			public void run() 
			{
				// This is client side. Update the configuration.
				((ClientProxy)FromTheDepths.proxy).serverConfiguration =  ModConfiguration.getFromNBTTagCompound(message.getMessageTag());
				
				ModConfiguration config = ((ClientProxy)FromTheDepths.proxy).getServerConfiguration();
				 				
				// Show a message to this player if their version is old.
				if (UpdateChecker.showMessage)
				{
					Minecraft.getMinecraft().player.sendMessage(new TextComponentString(UpdateChecker.messageToShow));
				}
			}
		});

		// no response in this case
		return null;
	}
}