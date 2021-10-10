package com.wuest.from_the_depths;

import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import com.wuest.from_the_depths.render.TileEntityAltarOfSpawningSpecialRenderer;
import com.wuest.from_the_depths.resource_loader.TotemTextureLoader;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.IOException;
import java.util.List;

public class ClientModRegistry extends ModRegistry {
    public static TileEntityAltarOfSpawningSpecialRenderer AltarOfSpawningRenderer;
    public static TotemTextureLoader totemTextureLoader;

    public static void RegisterSpecialRenderers() {
        ClientModRegistry.AltarOfSpawningRenderer = new TileEntityAltarOfSpawningSpecialRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAltarOfSpawning.class, ClientModRegistry.AltarOfSpawningRenderer);
    }

    public static void generateItemModels() {
        for (SpawnInfo spawnInfo : ModRegistry.SpawnInfos) {
            // Generate Custom ItemModel
            try {
                TotemTextureLoader.generateItemModels(spawnInfo.key);
            } catch (IOException e) {
                FromTheDepths.logger.warn(e.getMessage());
            }
        }
    }

    public static void registerTotemTextureLoader() {
        // Add Totem Textures Loader
        List<IResourcePack> defaultReosurcePacks =
                ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "field_110449_ao");
        ClientModRegistry.totemTextureLoader = new TotemTextureLoader();
        defaultReosurcePacks.add(ClientModRegistry.totemTextureLoader);
    }
}
