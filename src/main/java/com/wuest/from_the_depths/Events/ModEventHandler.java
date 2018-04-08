package com.wuest.from_the_depths.Events;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.wuest.from_the_depths.BuildingMethods;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Proxy.ClientProxy;
import com.wuest.from_the_depths.Proxy.Messages.ConfigSyncMessage;
import com.wuest.from_the_depths.StructureGen.BuildBlock;
import com.wuest.from_the_depths.StructureGen.Structure;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This is the server side event hander.
 * @author WuestMan
 */
@EventBusSubscriber(value = {Side.SERVER, Side.CLIENT })
public class ModEventHandler
{
	
	/**
	 * Contains a hashmap for the structures to build and for whom.
	 */
	public static HashMap<EntityPlayer, ArrayList<Structure>> structuresToBuild = new HashMap<EntityPlayer, ArrayList<Structure>>();
	
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
	 * This event is primarily used to build 100 blocks for any queued structures for all players.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		ArrayList<EntityPlayer> playersToRemove = new ArrayList<EntityPlayer>();
		
		for (Entry<EntityPlayer, ArrayList<Structure>> entry : ModEventHandler.structuresToBuild.entrySet())
		{
			ArrayList<Structure> structuresToRemove = new ArrayList<Structure>();
			
			// Build the first 100 blocks of each structure for this player.
			for (Structure structure : entry.getValue())
			{
				for (int i = 0; i < 100; i++)
				{
					// Structure clearing happens before anything else.
					if (structure.clearedBlockPos.size() > 0)
					{
						BlockPos currentPos = structure.clearedBlockPos.get(0);
						structure.clearedBlockPos.remove(0);
						
						IBlockState clearBlockState = structure.world.getBlockState(currentPos);
						
						// If this block is not specifically air then set it to air.
						// This will also break other mod's logic blocks but they would probably be broken due to structure generation anyways.
						if (clearBlockState.getBlock() != Blocks.AIR)
						{
							structure.world.setBlockToAir(currentPos);
						}
						else
						{
							// This is just an air block, move onto the next block don't need to wait for the next tick.
							i--;
						}
						
						continue;
					}
					
					BuildBlock currentBlock = null;
					
					if (structure.priorityOneBlocks.size() > 0)
					{
						currentBlock = structure.priorityOneBlocks.get(0);
						structure.priorityOneBlocks.remove(0);
					}
					else if (structure.priorityTwoBlocks.size() > 0)
					{
						currentBlock = structure.priorityTwoBlocks.get(0);
						structure.priorityTwoBlocks.remove(0);
					}
					else if (structure.priorityThreeBlocks.size() > 0)
					{
						currentBlock = structure.priorityThreeBlocks.get(0);
						structure.priorityThreeBlocks.remove(0);
					}
					else
					{
						// There are no more blocks to set.
						structuresToRemove.add(structure);
						break;
					}
					
					IBlockState state = currentBlock.getBlockState();
					
					BuildingMethods.ReplaceBlock(structure.world, currentBlock.getStartingPosition().getRelativePosition(structure.originalPos, structure.configuration.houseFacing), state);
					
					// After placing the initial block, set the sub-block. This needs to happen as the list isn't always in the correct order.
					if (currentBlock.getSubBlock() != null)
					{
						BuildBlock subBlock = currentBlock.getSubBlock();
						
						BuildingMethods.ReplaceBlock(structure.world, subBlock.getStartingPosition().getRelativePosition(structure.originalPos, structure.configuration.houseFacing), subBlock.getBlockState());
					}
				}
			}
			
			for (Structure structure : structuresToRemove)
			{
				// This structure is done building. Do any post-building operations.
				structure.AfterBuilding(structure.configuration, structure.world, structure.originalPos, structure.assumedNorth, entry.getKey());
				entry.getValue().remove(structure);
			}
			
			if (entry.getValue().size() == 0)
			{
				playersToRemove.add(entry.getKey());
			}
		}
		
		// Remove each player that has their structure's built.
		for (EntityPlayer player : playersToRemove)
		{
			ModEventHandler.structuresToBuild.remove(player);
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