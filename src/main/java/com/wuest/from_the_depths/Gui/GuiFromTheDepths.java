package com.wuest.from_the_depths.Gui;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.Config.ModConfiguration;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * 
 * @author WuestMan
 *
 */
public class GuiFromTheDepths extends GuiConfig
{
	public GuiFromTheDepths(GuiScreen parent)
	{
		super(parent,
				new ConfigElement(FromTheDepths.config.getCategory(ModConfiguration.OPTIONS)).getChildElements(),
				FromTheDepths.MODID, null, false, false, GuiConfig.getAbridgedConfigPath(FromTheDepths.config.toString()), null);
	}

	@Override
	public void initGui()
	{
		if (this.entryList == null || this.needsRefresh)
		{
			this.entryList = new GuiConfigEntries(this, mc);
			this.needsRefresh = false;
		}

		super.initGui();
	}
}