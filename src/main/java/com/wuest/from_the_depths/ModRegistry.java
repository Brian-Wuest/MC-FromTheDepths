package com.wuest.from_the_depths;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wuest.from_the_depths.Blocks.BlockAltarOfSpawning;
import com.wuest.from_the_depths.EntityInfo.SpawnInfo;
import com.wuest.from_the_depths.Items.ItemTotemOfSpawning;
import com.wuest.from_the_depths.Items.Structures.ItemChickenCoop;
import com.wuest.from_the_depths.Proxy.Messages.ConfigSyncMessage;
import com.wuest.from_the_depths.Proxy.Messages.PlayerEntityTagMessage;
import com.wuest.from_the_depths.Proxy.Messages.StructureTagMessage;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.ConfigSyncHandler;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.PlayerEntityHandler;
import com.wuest.from_the_depths.Proxy.Messages.Handlers.StructureHandler;
import com.wuest.from_the_depths.TileEntities.TileEntityAltarOfSpawning;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This is the mod registry so there is a way to get to all instances of the
 * blocks/items created by this mod.
 * 
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
	 * THe ArrayList of mod spawn infos.
	 */
	public static ArrayList<SpawnInfo> SpawnInfos = new ArrayList<SpawnInfo>();

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

	public static BlockAltarOfSpawning AlterOfSpawning()
	{
		return ModRegistry.GetBlock(BlockAltarOfSpawning.class);
	}

	public static ItemChickenCoop ChickenCoop()
	{
		return ModRegistry.GetItem(ItemChickenCoop.class);
	}

	/**
	 * Gets the item from the ModItems collections.
	 * 
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
				return (T) entry;
			}
		}

		return null;
	}

	/**
	 * Gets the block from the ModBlockss collections.
	 * 
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
				return (T) entry;
			}
		}

		return null;
	}

	/**
	 * Gets the gui screen for the ID and passes position data to it.
	 * 
	 * @param id The ID of the screen to get.
	 * @param x The X-Axis of where this screen was created from, this is used
	 *            to create a BlockPos.
	 * @param y The Y-Axis of where this screen was created from, this is used
	 *            to create a BlockPos.
	 * @param z The Z-Axis of where this screen was created from, this is used
	 *            to create a BlockPos.
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
					return (GuiScreen) entry.getValue()
							.getConstructor(int.class, int.class, int.class)
							.newInstance(x, y, z);
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
	 * This is where all in-game mod components (Items, Blocks) will be
	 * registered.
	 */
	public static void RegisterModComponents()
	{
		// ModRegistry.registerItem(new ItemChickenCoop("item_chicken_coop"));

		try
		{
			Block block = new BlockAltarOfSpawning("block_altar_of_summoning");
			ModRegistry.registerBlock(block);
		}
		catch (Exception ex)
		{
			FMLLog.getLogger().warn(ex.getMessage());
		}

		GameRegistry.registerTileEntity(TileEntityAltarOfSpawning.class,
				"block_altar_of_summoning");

		ModRegistry.registerItem(
				new ItemTotemOfSpawning("item_totem_of_summoning"));
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
		FromTheDepths.network.registerMessage(ConfigSyncHandler.class,
				ConfigSyncMessage.class, 1, Side.CLIENT);
		FromTheDepths.network.registerMessage(StructureHandler.class,
				StructureTagMessage.class, 2, Side.SERVER);

		FromTheDepths.network.registerMessage(PlayerEntityHandler.class,
				PlayerEntityTagMessage.class, 3, Side.CLIENT);
	}

	/**
	 * This is where mod capabilities are registered.
	 */
	public static void RegisterCapabilities()
	{
		// Register the dimension home capability.
		// CapabilityManager.INSTANCE.register(IStructureConfigurationCapability.class,
		// new StructureConfigurationStorage(),
		// StructureConfigurationCapability.class);
	}

	/**
	 * This method is used to register spawning information from the mod
	 * directory.
	 */
	public static void RegisterSpawningInfo()
	{
		Gson GSON = new GsonBuilder().create();

		try
		{
			if (FromTheDepths.proxy.spawnInfoFile.exists())
			{
				String fileContents = Files.toString(
						FromTheDepths.proxy.spawnInfoFile,
						Charset.defaultCharset());
				
				SpawnInfo[] infos = GSON.fromJson(fileContents,
						SpawnInfo[].class);

				for (SpawnInfo info : infos)
				{
					/*
					 * if (info.bossInfo.nbtData != null) {
					 * info.bossInfo.testData =
					 * JsonToNBT.getTagFromJson(info.bossInfo.nbtData.toString()
					 * ); }
					 * 
					 * if (info.bossAddInfo != null && info.bossAddInfo.nbtData
					 * != null) { info.bossAddInfo.testData =
					 * JsonToNBT.getTagFromJson(info.bossAddInfo.nbtData.
					 * toString()); }
					 */

					ModRegistry.SpawnInfos.add(info);
				}
			}
		}
		catch (JsonParseException e)
		{
			FromTheDepths.logger.error("Parsing error loading spawning information. {}",
					e);
		}
		catch (IOException e)
		{
			FromTheDepths.logger.error("Error loading spawning information file. {}", e);
		}
	}

	/**
	 * This method is used to register totem of summoning recipes.
	 */
	public static void RegisterTotemOfSummoningRecipes()
	{
		JsonContext ctx = new JsonContext(FromTheDepths.MODID);
		Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
		boolean loadedRecipe = false;

		ArrayList<ResourceLocation> entityInfos = new ArrayList<ResourceLocation>();

		for (File file : FromTheDepths.proxy.modDirectory.toFile().listFiles())
		{
			if (file.isFile())
			{
				Path path = file.toPath();
				String name = Files.getNameWithoutExtension(file.getName());

				// Don't include the spawning information file.
				if (!name.contains("spawnInfo"))
				{
					ResourceLocation key = new ResourceLocation(ctx.getModId(),
							name);

					try
					{
						String fileContents = Files.toString(file,
								Charset.defaultCharset());

						JsonObject json = JsonUtils.fromJson(GSON, fileContents,
								JsonObject.class, true);

						IRecipe recipe = CraftingHelper.getRecipe(json, ctx);
						ItemStack recipeOutput = recipe.getRecipeOutput();

						// Limit recipe registration to ONLY the items which are
						// totems of spawning.
						if (recipeOutput
								.getItem() instanceof ItemTotemOfSpawning)
						{
							String recipeCompound = ModRegistry
									.TotemOfSpawning()
									.getEntityKeyFromItemStack(recipeOutput);

							if (recipeCompound == null)
							{
								FromTheDepths.logger.warn(
										"From_The_Depths: Summoning recipe found at location [{}] has output which doesn't contain valid nbt data or specifies a boss which doesn't exist.",
										path.toString());
							}
							else
							{
								ForgeRegistries.RECIPES
										.register(recipe.setRegistryName(key));
								
								ModRegistry.TotemOfSpawning().subItems
										.add(recipeOutput);
								
								loadedRecipe = true;
							}
						}
					}
					catch (JsonParseException e)
					{
						FromTheDepths.logger.error(
								"From_The_Depths: Parsing error loading recipe {}. {}",
								key, e);
					}
					catch (Exception e)
					{
						FromTheDepths.logger.error(
								"From_The_Depths: Error loading recipe {}. {}",
								key, e);
					}
				}
			}
		}

		if (loadedRecipe)
		{
			FMLCommonHandler.instance().resetClientRecipeBook();
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
	 * 
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
	 * 
	 * @param <T> The type of block to register.
	 * @param block The block to register.
	 * @param includeItemBlock True to include a default item block.
	 * @return The block which was registered.
	 */
	public static <T extends Block> T registerBlock(T block,
			boolean includeItemBlock)
	{
		if (includeItemBlock)
		{
			ModItems.add(new ItemBlock(block)
					.setRegistryName(block.getRegistryName()));
		}

		ModRegistry.ModBlocks.add(block);

		return block;
	}

	/**
	 * Registers a block in the game registry.
	 * 
	 * @param <T> The type of block to register.
	 * @param <I> The type of item block to register.
	 * @param block The block to register.
	 * @param itemBlock The item block to register with the block.
	 * @return The block which was registered.
	 */
	public static <T extends Block, I extends ItemBlock> T registerBlock(
			T block, I itemBlock)
	{
		ModRegistry.ModBlocks.add(block);

		if (itemBlock != null)
		{
			ModRegistry.ModItems.add(itemBlock);
		}

		return block;
	}

	/**
	 * Set the registry name of {@code item} to {@code itemName} and the
	 * un-localised name to the full registry name.
	 *
	 * @param item The item
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
	 * Set the registry name of {@code block} to {@code blockName} and the
	 * un-localised name to the full registry name.
	 *
	 * @param block The block
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
		ModRegistry.ModGuis.put(ModRegistry.GuiChickenCoop,
				com.wuest.from_the_depths.Gui.Structures.GuiChickenCoop.class);
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and
	 * not mod-specific objects.
	 * 
	 * @param name The name of the recipe. ModID is pre-pended to it.
	 * @param stack The output of the recipe.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapedRecipes AddShapedRecipe(String name, String groupName,
			ItemStack stack, Object... recipeComponents)
	{
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;

		ShapedPrimer primer = CraftingHelper.parseShaped(recipeComponents);
		ShapedRecipes shapedrecipes = new ShapedRecipes(groupName, primer.width,
				primer.height, primer.input, stack);
		shapedrecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapedrecipes);

		return shapedrecipes;
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and
	 * not mod-specific objects.
	 * 
	 * @param name The name of the recipe.
	 * @param stack The output stack.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapelessRecipes AddShapelessRecipe(String name,
			String groupName, ItemStack stack, Object... recipeComponents)
	{
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;
		NonNullList<Ingredient> list = NonNullList.create();

		for (Object object : recipeComponents)
		{
			if (object instanceof ItemStack)
			{
				list.add(Ingredient.fromStacks(((ItemStack) object).copy()));
			}
			else if (object instanceof Item)
			{
				list.add(Ingredient.fromStacks(new ItemStack((Item) object)));
			}
			else
			{
				if (!(object instanceof Block))
				{
					throw new IllegalArgumentException(
							"Invalid shapeless recipe: unknown type "
									+ object.getClass().getName() + "!");
				}

				list.add(Ingredient.fromStacks(new ItemStack((Block) object)));
			}
		}

		ShapelessRecipes shapelessRecipes = new ShapelessRecipes(groupName,
				stack, list);
		shapelessRecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapelessRecipes);

		return shapelessRecipes;
	}
}