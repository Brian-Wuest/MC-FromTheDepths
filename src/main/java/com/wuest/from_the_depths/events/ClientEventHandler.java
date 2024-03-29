package com.wuest.from_the_depths.events;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.items.ItemTotemOfSpawning;
import com.wuest.from_the_depths.proxy.ClientProxy;
import com.wuest.from_the_depths.resource_loader.TotemTextureLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

/**
 * @author WuestMan
 */
@EventBusSubscriber(value = {Side.CLIENT})
public class ClientEventHandler {

    /**
     * This is used to clear out the server configuration on the client side.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public static void OnClientDisconnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // When the player logs out, make sure to re-set the server configuration.
        // This is so a new configuration can be successfully loaded when they switch
        // servers or worlds (on single player.
        ((ClientProxy) FromTheDepths.proxy).serverConfiguration = null;
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Block block : ModRegistry.ModBlocks) {
            ClientEventHandler.regBlock(block);
        }

        for (Item item : ModRegistry.ModItems) {
            ClientEventHandler.regItem(item);
        }
    }

    /**
     * Registers an item to be rendered. This is needed for textures.
     *
     * @param item The item to register.
     */
    public static void regItem(Item item) {

        if (item instanceof ItemTotemOfSpawning) {
            ItemTotemOfSpawning totem = ((ItemTotemOfSpawning) item);

            if (totem.key != null && FromTheDepths.proxy.modDirectory.resolve("textures" + File.separatorChar + totem.key + ".png").toFile().exists()) {
                FromTheDepths.logger.info("Boss with name: " + totem.key + " got custom texture");
                ClientEventHandler.regItem(item, 0, TotemTextureLoader.DOMAIN + ':' + totem.key);
            } else {
                FromTheDepths.logger.info("Boss with name: " + totem.key + " got default texture");
                ClientEventHandler.regItem(item, 0, FromTheDepths.MODID + ":item_totem_of_summoning");
            }
        }
    }

    /**
     * Registers an item to be rendered. This is needed for textures.
     *
     * @param item      The item to register.
     * @param metaData  The meta-data for the item to register.
     * @param blockName the name of the block.
     */
    public static void regItem(Item item, int metaData, String blockName) {
        ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(blockName), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, metaData, location);
    }

    /**
     * Registers a block to be rendered. This is needed for textures.
     *
     * @param block The block to register.
     */
    @SuppressWarnings("deprecation")
    public static void regBlock(Block block) {
        NonNullList<ItemStack> stacks = NonNullList.create();

        Item itemBlock = Item.getItemFromBlock(block);

        // If there are sub-blocks for this block, register each of them.
        block.getSubBlocks(CreativeTabs.MISC, stacks);

        if (stacks.size() > 0) {
            for (ItemStack stack : stacks) {
                Block subBlock = block.getStateFromMeta(stack.getMetadata()).getBlock();
                String name = subBlock.getRegistryName().toString();

                ClientEventHandler.regItem(stack.getItem(), stack.getMetadata(), name);
            }
        } else {
            ClientEventHandler.regItem(itemBlock);
        }
    }

}
