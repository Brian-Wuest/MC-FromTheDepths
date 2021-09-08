package com.wuest.from_the_depths.gui;

import com.wuest.from_the_depths.FromTheDepths;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;

/**
 * 
 * @author WuestMan
 *
 */
public class ConfigGuiFactory extends DefaultGuiFactory {
  public ConfigGuiFactory() {
    super(FromTheDepths.MODID, "From The Depths");
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new GuiFromTheDepths(parentScreen);
  }
}