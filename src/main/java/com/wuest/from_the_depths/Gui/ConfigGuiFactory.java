package com.wuest.from_the_depths.Gui;

import java.util.Set;

import com.wuest.from_the_depths.FromTheDepths;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * 
 * @author WuestMan
 *
 */
public class ConfigGuiFactory extends DefaultGuiFactory 
{
	public ConfigGuiFactory() 
	{
		super(FromTheDepths.MODID, "From The Depths");
	}
	
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {  
        return new GuiFromTheDepths(parentScreen);
    }
}