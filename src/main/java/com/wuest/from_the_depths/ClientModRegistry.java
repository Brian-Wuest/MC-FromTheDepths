package com.wuest.from_the_depths;

import com.wuest.from_the_depths.render.TileEntityAltarOfSpawningSpecialRenderer;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientModRegistry extends ModRegistry
{
	public static TileEntityAltarOfSpawningSpecialRenderer AltarOfSpawningRenderer;
	
	public static void RegisterSpecialRenderers()
	{
		ClientModRegistry.AltarOfSpawningRenderer = new TileEntityAltarOfSpawningSpecialRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAltarOfSpawning.class, ClientModRegistry.AltarOfSpawningRenderer);
	}
}
