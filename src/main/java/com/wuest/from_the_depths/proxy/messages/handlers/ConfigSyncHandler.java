package com.wuest.from_the_depths.proxy.messages.handlers;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.blocks.BlockAltarOfSpawning;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.proxy.ClientProxy;
import com.wuest.from_the_depths.proxy.messages.ConfigSyncMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author WuestMan
 *
 */
public class ConfigSyncHandler implements IMessageHandler<ConfigSyncMessage, IMessage> {
  @Override
  public IMessage onMessage(final ConfigSyncMessage message, final MessageContext ctx) {
    // Or Minecraft.getMinecraft() on the client.
    IThreadListener mainThread = null;

    if (ctx.side.isClient()) {
      mainThread = Minecraft.getMinecraft();
    } else {
      mainThread = (WorldServer) ctx.getServerHandler().player.world;
    }

    mainThread.addScheduledTask(new Runnable() {
      @Override
      public void run() {
        // This is client side. Update the configuration.
        ((ClientProxy) FromTheDepths.proxy).serverConfiguration = ModConfiguration
            .getFromNBTTagCompound(message.getMessageTag());

        // ModConfiguration config = ((ClientProxy) FromTheDepths.proxy).getServerConfiguration();

        BlockAltarOfSpawning.SetBreakableStatus();
      }
    });

    // no response in this case
    return null;
  }
}