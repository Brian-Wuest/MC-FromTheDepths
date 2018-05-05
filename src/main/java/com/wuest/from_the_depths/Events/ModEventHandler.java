package com.wuest.from_the_depths.Events;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Proxy.ClientProxy;
import com.wuest.from_the_depths.Proxy.Messages.ConfigSyncMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This is the server side event hander.
 * @author WuestMan
 */
@EventBusSubscriber(value = {Side.SERVER, Side.CLIENT })
public class ModEventHandler
{	
	/**
	 * This event occurs when a player logs in. This is used to send server configuration to the client.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void onPlayerLoginEvent(PlayerLoggedInEvent event)
	{
		if(!event.player.world.isRemote)
		{
			NBTTagCompound tag = FromTheDepths.proxy.proxyConfiguration.ToNBTTagCompound();
			FromTheDepths.network.sendTo(new ConfigSyncMessage(tag), (EntityPlayerMP)event.player);
			System.out.println("Sent config to '" + event.player.getDisplayNameString() + ".'");
			
/*			ArrayList<String> tags = new ArrayList<String>();
			for (ResourceLocation location :  EntityList.getEntityNameList())
			{
				Entity entity = EntityList.createEntityByIDFromName(location, event.player.world);
				
				if (entity instanceof EntityLivingBase)
				{
					tags.add(entity.serializeNBT().toString());
				}
			}
			
			BufferedWriter writer =  null;
			try
			{
				writer = Files.newWriter(FromTheDepths.proxy.spawnInfoFile, Charset.defaultCharset());
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (writer != null)
			{
				try
				{
					writer.write("[");
					
					for (String string : tags)
					{
						writer.write(string);
						writer.write(",");
					}
					
					writer.write("]");
					
					writer.flush();
					writer.close();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}*/
		}
	}
	
	/**
	 * This event is used to clear out the server configuration for clients that log off the server.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event)
	{
		// When the player logs out, make sure to re-set the server configuration. 
		// This is so a new configuration can be successfully loaded when they switch servers or worlds (on single player.
		if (event.player.world.isRemote)
		{
			// Make sure to null out the server configuration from the client.
			((ClientProxy)FromTheDepths.proxy).serverConfiguration = null;
			ModRegistry.TotemOfSpawning().serverSubItems.clear();
		}
	}
	
	/**
	 * This is used to sync up the configuration when it's change by the user.
	 * @param onConfigChangedEvent The event object.
	 */
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent)
	{
		if(onConfigChangedEvent.getModID().equals(FromTheDepths.MODID))
		{
			ModConfiguration.syncConfig();
		}
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{	
		event.getRegistry().registerAll(ModRegistry.ModBlocks.toArray(new Block[ModRegistry.ModBlocks.size()]));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{	
		event.getRegistry().registerAll(ModRegistry.ModItems.toArray(new Item[ModRegistry.ModItems.size()]));
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{		
		// Register the ore dictionary blocks.
		ModRegistry.RegisterOreDictionaryRecords();
	}
}