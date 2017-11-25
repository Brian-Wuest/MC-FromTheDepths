package com.wuest.from_the_depths.Events;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.Config.EntityPlayerConfiguration;
import com.wuest.from_the_depths.Config.ModConfiguration;
import com.wuest.from_the_depths.Proxy.ClientProxy;
import com.wuest.from_the_depths.Render.StructureRenderHandler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @author WuestMan
 *
 */
@EventBusSubscriber(value = { Side.CLIENT })
public class ClientEventHandler
{
	/**
	 * Determines how long a shader has been running.
	 */
	public static int ticksInGame;
	
	/**
	 * This client event handler is used to store player specific data.
	 */
	public static EntityPlayerConfiguration playerConfig = new EntityPlayerConfiguration();

	/**
	 * The world render last event. This is used for structure rendering.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void onWorldRenderLast(RenderWorldLastEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.player != null && mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null && (!mc.player.isSneaking()))
		{
			StructureRenderHandler.renderPlayerLook(mc.player, mc.objectMouseOver);
		}
	}

	/**
	 * The player right-click block event. This is used to stop the structure rendering for the preview.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getWorld().isRemote) 
		{
			if (StructureRenderHandler.currentStructure != null && event.getEntityPlayer() == Minecraft.getMinecraft().player)
			{
				StructureRenderHandler.setStructure(null, EnumFacing.NORTH, null);
				event.setCanceled(true);
			}
		}
	}
	
	/**
	 * This is used to clear out the server configuration on the client side.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void OnClientDisconnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
	{
		// When the player logs out, make sure to re-set the server configuration. 
	 	// This is so a new configuration can be successfully loaded when they switch servers or worlds (on single player.
	 	((ClientProxy)FromTheDepths.proxy).serverConfiguration = null;
	}
	
	/**
	 * This is used to increment the ticks in game value.
	 * @param event The event object.
	 */
	@SubscribeEvent
	public static void ClientTickEnd(ClientTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			
			if (gui == null || !gui.doesGuiPauseGame()) 
			{
				// Reset the ticks in game if we are getting close to the maximum value of an integer.
				if (Integer.MAX_VALUE - 100 == ClientEventHandler.ticksInGame)
				{
					ClientEventHandler.ticksInGame = 1;
				}
				
				ClientEventHandler.ticksInGame++;
			}
		}
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		for (Block block: ModRegistry.ModBlocks)
		{
			ClientEventHandler.regBlock(block);
		}
		
		for (Item item: ModRegistry.ModItems)
		{
			ClientEventHandler.regItem(item);
		}
	}
	
	/**
	 * Registers an item to be rendered. This is needed for textures.
	 * @param item The item to register.
	 */
	public static void regItem(Item item) 
	{
		ClientEventHandler.regItem(item, 0, item.getUnlocalizedName().substring(5));
	}
	
	/**
	 * Registers an item to be rendered. This is needed for textures.
	 * @param item The item to register.
	 * @param metaData The meta data for the item to register.
	 * @param blockName the name of the block.
	 */
	public static void regItem(Item item, int metaData, String blockName)
	{
		ModelResourceLocation location = new ModelResourceLocation(blockName, "inventory");
		//System.out.println("Registering Item: " + location.getResourceDomain() + "[" + location.getResourcePath() + "]");
		
		ModelLoader.setCustomModelResourceLocation(item, metaData, location);
	}

	/**
	 * Registers a block to be rendered. This is needed for textures.
	 * @param block The block to register.
	 */
	public static void regBlock(Block block)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		
		Item itemBlock = Item.getItemFromBlock(block);
		
		// If there are sub-blocks for this block, register each of them.
		block.getSubBlocks(null, stacks);
		
		if (itemBlock != null)
		{
			if (stacks.size() > 0)
			{
				for (ItemStack stack : stacks)
				{
					Block subBlock = block.getStateFromMeta(stack.getMetadata()).getBlock();
					String name = subBlock.getRegistryName().toString();
					
					ClientEventHandler.regItem(stack.getItem(), stack.getMetadata(), name);
				}
			}
			else
			{
				ClientEventHandler.regItem(itemBlock);
			}
		}
	}

}
