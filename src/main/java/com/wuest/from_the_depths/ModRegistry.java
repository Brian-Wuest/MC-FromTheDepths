package com.wuest.from_the_depths;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wuest.from_the_depths.Blocks.BlockAlterOfSpawning;
import com.wuest.from_the_depths.Items.ItemTotemOfSpawning;
import com.wuest.from_the_depths.Items.Structures.ItemChickenCoop;
import com.wuest.from_the_depths.Proxy.Messages.ConfigSyncMessage;
import com.wuest.from_the_depths.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.from_the_depths.Proxy.Messages.StructureTagMessage;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.ConfigSyncHandler;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.PlayerEntityHandler;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.StructureHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.Tuple2;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 * @author WuestMan
 *
 */
public class ModRegistry
{
	/**
	 * The ArrayList of mod registered items.
	 */
	public static ArrayList<Item> ModItems = new ArrayList<Item>();

	/**
	 * The ArrayList of mod registered blocks.
	 */
	public static ArrayList<Block> ModBlocks = new ArrayList<Block>();

	/**
	 * The hashmap of mod guis.
	 */
	public static HashMap<Integer, Class> ModGuis = new HashMap<Integer, Class>();

	/**
	 * The identifier for the ChickenCoop GUI.
	 */
	public static final int GuiChickenCoop = 1;

	/**
	 * Static constructor for the mod registry.
	 */
	static
	{
		ModRegistry.RegisterModComponents();
	}

	public static ItemTotemOfSpawning TotemOfSpawning()
	{
		return ModRegistry.GetItem(ItemTotemOfSpawning.class);
	}
	
	public static BlockAlterOfSpawning AlterOfSpawning()
	{
		return ModRegistry.GetBlock(BlockAlterOfSpawning.class);
	}
	
 	public static ItemChickenCoop ChickenCoop()
	{
		return ModRegistry.GetItem(ItemChickenCoop.class);
	}
	
	/**
	 * Gets the item from the ModItems collections.
	 * @param <T> The type which extends item.
	 * @param genericClass The class of item to get from the collection.
	 * @return Null if the item could not be found otherwise the item found.
	 */
	public static <T extends Item> T GetItem(Class<T> genericClass)
	{
		for (Item entry : ModRegistry.ModItems)
		{
			if (entry.getClass() == genericClass)
			{
				return (T)entry;
			}
		}

		return null;
	}

	/**
	 * Gets the block from the ModBlockss collections.
	 * @param <T> The type which extends Block.
	 * @param genericClass The class of block to get from the collection.
	 * @return Null if the block could not be found otherwise the block found.
	 */
	public static <T extends Block> T GetBlock(Class<T> genericClass)
	{
		for (Block entry : ModRegistry.ModBlocks)
		{
			if (entry.getClass() == genericClass)
			{
				return (T)entry;
			}
		}

		return null;
	}

	/**
	 * Gets the gui screen for the ID and passes position data to it.
	 * @param id The ID of the screen to get.
	 * @param x The X-Axis of where this screen was created from, this is used to create a BlockPos.
	 * @param y The Y-Axis of where this screen was created from, this is used to create a BlockPos.
	 * @param z The Z-Axis of where this screen was created from, this is used to create a BlockPos.
	 * @return Null if the screen wasn't found, otherwise the screen found.
	 */
	public static GuiScreen GetModGuiByID(int id, int x, int y, int z)
	{
		for (Entry<Integer, Class> entry : ModRegistry.ModGuis.entrySet())
		{
			if (entry.getKey() == id)
			{
				try
				{
					return (GuiScreen)entry.getValue().getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
				}
				catch (InstantiationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalArgumentException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (NoSuchMethodException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (SecurityException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	/**
	 * This is where all in-game mod components (Items, Blocks) will be registered.
	 */
	public static void RegisterModComponents()
	{
		ModRegistry.registerItem(new ItemChickenCoop("item_chicken_coop"));
		
		ModRegistry.registerBlock(new BlockAlterOfSpawning("block_alter_of_spawning"));
		ModRegistry.registerItem(new ItemTotemOfSpawning("item_totem_of_spawning"));
	}

	/**
	 * Registers records into the ore dictionary.
	 */
	public static void RegisterOreDictionaryRecords()
	{
		// Register certain blocks into the ore dictionary.
	}
	
	/**
	 * This is where the mod messages are registered.
	 */
	public static void RegisterMessages()
	{
		FromTheDepths.network.registerMessage(ConfigSyncHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		FromTheDepths.network.registerMessage(StructureHandler.class, StructureTagMessage.class, 2, Side.SERVER);
		
		FromTheDepths.network.registerMessage(PlayerEntityHandler.class, PlayerEntityTagMessage.class, 3, Side.CLIENT);
	}

	/**
	 * This is where mod capabilities are registered.
	 */
	public static void RegisterCapabilities()
	{
		// Register the dimension home capability.
		//CapabilityManager.INSTANCE.register(IStructureConfigurationCapability.class, new StructureConfigurationStorage(), StructureConfigurationCapability.class);
	}

	/**
	 * This method is used to register totem of summoning recipes.
	 */
	public static void RegisterTotemOfSummoningRecipes() 
	{
		DirectoryStream<Path> stream = null;
		JsonContext ctx = new JsonContext(FromTheDepths.MODID);
		Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		boolean loadedRecipe = false;
		
		try
		{
			stream = java.nio.file.Files.newDirectoryStream(FromTheDepths.proxy.modDirectory.toPath());
			ArrayList<ResourceLocation> entityInfos = new ArrayList<ResourceLocation>();
			
			for (Path path : stream)
			{
				File file = path.toFile();
				
				if (file.isFile())
				{
					String name = FilenameUtils.getBaseName(path.toString());
					BufferedReader reader = null;
					ResourceLocation key = new ResourceLocation(ctx.getModId(), name);
					reader = Files.newBufferedReader(path);
					
					try
					{
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
						ItemStack recipeOutput = recipe.getRecipeOutput();
						
						// Limit recipe registration to ONLY the items which are totems of spawning.
						if (recipeOutput.getItem() instanceof ItemTotemOfSpawning)
						{
							ResourceLocation recipeCompound = ModRegistry.TotemOfSpawning().getEntityResourceNameFromItemStack(recipeOutput);
							
							if (recipeCompound != null)
							{
								boolean foundExistingRegisteredEntity = false;
								
								for (ResourceLocation entityInfo : entityInfos)
								{
									if (entityInfo.getResourceDomain().equals(recipeCompound.getResourceDomain())
											&& entityInfo.getResourcePath().equals(recipeCompound.getResourcePath()))
									{
										foundExistingRegisteredEntity = true;
										
										FMLLog.log.warn("Summoning recipe found at location [" + path.toString() + "] specifies an entity which was already registered.");
										break;
									}
								}
								
								if (!foundExistingRegisteredEntity)
								{
									ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
									ModRegistry.TotemOfSpawning().subItems.add(recipeOutput);
									entityInfos.add(recipeCompound);
								}
							}
							else
							{
								FMLLog.log.warn("Summoning recipe found at location [" + path.toString() + "] has output which doesn't contain valid nbt data.");
							}
						}
					}
					catch (JsonParseException e)
	                {
	                    FMLLog.log.error("Parsing error loading recipe {}", key, e);
	                }
					finally
	                {
	                    IOUtils.closeQuietly(reader);
	                }
				}
			}
			
			if (loadedRecipe)
			{
				FMLCommonHandler.instance().resetClientRecipeBook();
			}
		}
		
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Register an Item
	 *
	 * @param item The Item instance
	 * @param <T> The Item type
	 * @return The Item instance
	 */
	public static <T extends Item> T registerItem(T item)
	{
		ModRegistry.ModItems.add(item);

		return item;
	}

	/**
	 * Registers a block in the game registry.
	 * @param <T> The type of block to register.
	 * @param block The block to register.
	 * @return The block which was registered.
	 */
	public static <T extends Block> T registerBlock(T block)
	{
		return ModRegistry.registerBlock(block, true);
	}

	/**
	 * Registers a block in the game registry.
	 * @param <T> The type of block to register.
	 * @param block The block to register.
	 * @param includeItemBlock True to include a default item block.
	 * @return The block which was registered.
	 */
	public static <T extends Block> T registerBlock(T block, boolean includeItemBlock)
	{
		if (includeItemBlock)
		{
			ModItems.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}

		ModRegistry.ModBlocks.add(block);

		return block;
	}

	/**
	 * Registers a block in the game registry.
	 * @param <T> The type of block to register.
	 * @param <I> The type of item block to register.
	 * @param block The block to register.
	 * @param itemBlock The item block to register with the block.
	 * @return The block which was registered.
	 */
	public static <T extends Block, I extends ItemBlock> T registerBlock(T block, I itemBlock)
	{
		ModRegistry.ModBlocks.add(block);

		if (itemBlock != null)
		{
			ModRegistry.ModItems.add(itemBlock);
		}

		return block;
	}

	/**
	 * Set the registry name of {@code item} to {@code itemName} and the un-localised name to the full registry name.
	 *
	 * @param item     The item
	 * @param itemName The item's name
	 */
	public static void setItemName(Item item, String itemName) 
	{
		if (itemName != null)
		{
			item.setRegistryName(itemName);
			item.setUnlocalizedName(item.getRegistryName().toString());
		}
	}

	/**
	 * Set the registry name of {@code block} to {@code blockName} and the un-localised name to the full registry name.
	 *
	 * @param block     The block
	 * @param blockName The block's name
	 */
	public static void setBlockName(Block block, String blockName) 
	{
		block.setRegistryName(blockName);
		block.setUnlocalizedName(block.getRegistryName().toString());
	}

	/**
	 * Adds all of the Mod Guis to the HasMap.
	 */
	public static void AddGuis()
	{
		ModRegistry.ModGuis.put(ModRegistry.GuiChickenCoop, com.wuest.from_the_depths.Gui.Structures.GuiChickenCoop.class);
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and not mod-specific objects.
	 * @param name The name of the recipe. ModID is pre-pended to it.
	 * @param stack The output of the recipe.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapedRecipes AddShapedRecipe(String name, String groupName, ItemStack stack, Object... recipeComponents)
	{	
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;

		ShapedPrimer primer = CraftingHelper.parseShaped(recipeComponents);
		ShapedRecipes shapedrecipes = new ShapedRecipes(groupName, primer.width, primer.height, primer.input, stack);
		shapedrecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapedrecipes);

		return shapedrecipes;
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and not mod-specific objects.
	 * @param name The name of the recipe.
	 * @param stack The output stack.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapelessRecipes AddShapelessRecipe(String name, String groupName, ItemStack stack, Object... recipeComponents)
	{
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;
		NonNullList<Ingredient> list = NonNullList.create();

		for (Object object : recipeComponents)
		{
			if (object instanceof ItemStack)
			{
				list.add(Ingredient.fromStacks(((ItemStack)object).copy()));
			}
			else if (object instanceof Item)
			{
				list.add(Ingredient.fromStacks(new ItemStack((Item)object)));
			}
			else
			{
				if (!(object instanceof Block))
				{
					throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
				}

				list.add(Ingredient.fromStacks(new ItemStack((Block)object)));
			}
		}

		ShapelessRecipes shapelessRecipes = new ShapelessRecipes(groupName, stack, list);
		shapelessRecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapelessRecipes);

		return shapelessRecipes;
	}
}