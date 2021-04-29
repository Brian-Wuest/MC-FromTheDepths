package com.wuest.from_the_depths.gui;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

/**
 * This class contains the keys for the language files.
 * @author WuestMan
 */
public class GuiLangKeys
{
	@Unlocalized(name = "General")
	public static final String GENERAL = "from_the_depths.gui.general";
	
	@Unlocalized(name = "Config")
	public static final String CONFIG = "from_the_depths.gui.tab.config";
	
	@Unlocalized(name = "Oak")
	public static final String WOOD_TYPE_OAK = "from_the_depths.wood.type.oak";
	
	@Unlocalized(name = "Spruce")
	public static final String WOOD_TYPE_SPRUCE = "from_the_depths.wood.type.spruce";
	
	@Unlocalized(name = "Birch")
	public static final String WOOD_TYPE_BIRCH = "from_the_depths.wood.type.birch";
	
	@Unlocalized(name = "Jungle")
	public static final String WOOD_TYPE_JUNGLE = "from_the_depths.wood.type.jungle";
	
	@Unlocalized(name = "Acacia")
	public static final String WOOD_TYPE_ACACIA = "from_the_depths.wood.type.acacia";
	
	@Unlocalized(name = "Dark Oak")
	public static final String WOOD_TYPE_DARK_OAK = "from_the_depths.wood.type.darkoak";
	
	@Unlocalized(name = "Structure Facing")
	public static final String GUI_STRUCTURE_FACING = "from_the_depths.gui.structure.facing";
	
	@Unlocalized(name = "Note: If you're facing north, choose south so the structure is facing you.")
	public static final String GUI_STRUCTURE_FACING_PLAYER="from_the_depths.gui.structure.facing.player";
	
	@Unlocalized(name = "Cannot build structure due to protected blocks/area.")
	public static final String GUI_STRUCTURE_NOBUILD = "from_the_depths.gui.structure.nobuild";
	
	@Unlocalized(name = "Build!")
	public static final String GUI_BUTTON_BUILD = "from_the_depths.gui.button.build";
	
	@Unlocalized(name = "Cancel")
	public static final String GUI_BUTTON_CANCEL = "from_the_depths.gui.button.cancel";
	
	@Unlocalized(name = "Preview!")
	public static final String GUI_BUTTON_PREVIEW = "from_the_depths.gui.button.preview";
	
	@Unlocalized(name = "Structure Complete!")
	public static final String GUI_PREVIEW_COMPLETE = "from_the_depths.gui.preview.complete";
	
	@Unlocalized(name = "Right-click on any block in the world to remove the preview.")
	public static final String GUI_PREVIEW_NOTICE = "from_the_depths.gui.preview.notice";
	
	@Unlocalized(name = "north")
	public static final String GUI_NORTH = "from_the_depths.gui.north";
	
	@Unlocalized(name = "south")
	public static final String GUI_SOUTH = "from_the_depths.gui.south";
	
	@Unlocalized(name = "east")
	public static final String GUI_EAST = "from_the_depths.gui.east";
	
	@Unlocalized(name = "west")
	public static final String GUI_WEST = "from_the_depths.gui.west";
	
	@Unlocalized(name = "white")
	public static final String GUI_WHITE = "from_the_depths.gui.white";
	
	@Unlocalized(name = "orange")
	public static final String GUI_ORANGE = "from_the_depths.gui.orange";
	
	@Unlocalized(name = "magenta")
	public static final String GUI_MAGENTA = "from_the_depths.gui.magenta";
	
	@Unlocalized(name = "light_blue")
	public static final String GUI_LIGHT_BLUE = "from_the_depths.gui.light_blue";
	
	@Unlocalized(name = "yellow")
	public static final String GUI_YELLOW = "from_the_depths.gui.yellow";
	
	@Unlocalized(name = "lime")
	public static final String GUI_LIME = "from_the_depths.gui.lime";
	
	@Unlocalized(name = "pink")
	public static final String GUI_PINK = "from_the_depths.gui.pink";
	
	@Unlocalized(name = "gray")
	public static final String GUI_GRAY = "from_the_depths.gui.gray";
	
	@Unlocalized(name = "silver")
	public static final String GUI_SILVER = "from_the_depths.gui.silver";
	
	@Unlocalized(name = "cyan")
	public static final String GUI_CYAN = "from_the_depths.gui.cyan";
	
	@Unlocalized(name = "purple")
	public static final String GUI_PURPLE = "from_the_depths.gui.purple";
	
	@Unlocalized(name = "blue")
	public static final String GUI_BLUE = "from_the_depths.gui.blue";
	
	@Unlocalized(name = "brown")
	public static final String GUI_BROWN = "from_the_depths.gui.brown";
	
	@Unlocalized(name = "green")
	public static final String GUI_GREEN = "from_the_depths.gui.green";
	
	@Unlocalized(name = "red")
	public static final String GUI_RED = "from_the_depths.gui.red";
	
	@Unlocalized(name = "black")
	public static final String GUI_BLACK = "from_the_depths.gui.black";
	

	/**
	 * Translates the specified language key for the current language.
	 * 
	 * @param translateKey The language key to use.
	 * @return The translated language key.
	 */
	public static String translateString(String translateKey)
	{
		if (I18n.hasKey(translateKey))
		{
			return I18n.format(translateKey, new Object[0]);
		}
		else
		{
			return GuiLangKeys.getUnLocalized(translateKey);
		}
	}

	/**
	 * Gets the unlocalized version of this translation key.
	 * If the translation key does not exist the key is returned instead of the unlocalized value.
	 * @param translateKey The translation key to get the unlocalized value for.
	 * @return The unlocalized value or the passed in key.
	 */
	public static String getUnLocalized(String translateKey)
	{
		for (Field field : GuiLangKeys.class.getDeclaredFields())
		{
			String value = "";
			
			try
			{
				value = field.get(null).toString();
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
			if (value.equals(translateKey))
			{
				Annotation[] annotations = field.getDeclaredAnnotations();
				
				for (Annotation annotation : annotations)
				{
					if (annotation instanceof Unlocalized)
					{
						
						return ((Unlocalized)annotation).name();
					}
				}
			}
		}

		return translateKey;
	}

	public static String translateFacing(EnumFacing facing)
	{
		return GuiLangKeys.translateString("from_the_depths.gui." + facing.getName2());
	}
	
	public static String translateDye(EnumDyeColor dyeColor)
	{
		return GuiLangKeys.translateString("from_the_depths.gui." + dyeColor.getUnlocalizedName());
	}
	
	/**
	 * An annotation which allows the UI to get the unlocalized name;
	 * @author WuestMan
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(java.lang.annotation.ElementType.FIELD)
	public @interface Unlocalized
	{
		public String name() default "";
	}
}