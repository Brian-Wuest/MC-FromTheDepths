package com.wuest.from_the_depths.Render;

import com.wuest.from_the_depths.TileEntities.TileEntityAltarOfSpawning;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.text.ITextComponent;

public class TileEntityAltarOfSpawningSpecialRenderer extends TileEntitySpecialRenderer<TileEntityAltarOfSpawning> {
	@Override
	public void render(TileEntityAltarOfSpawning te, double x, double y, double z, float partialTicks, int destroyStage,
					   float alpha) {
		ITextComponent itextcomponent = te.getDisplayName();

		if (itextcomponent != null) {
			this.setLightmapDisabled(true);
			this.drawNameplate(te, itextcomponent.getFormattedText(), x, y, z, 12);
			this.setLightmapDisabled(false);
		}
	}
}
