package com.wuest.from_the_depths;

import com.google.common.io.Files;
import com.google.gson.*;
import com.wuest.from_the_depths.blocks.BlockAltarOfSpawning;
import com.wuest.from_the_depths.davoleo.ResourceLocationTypeAdapter;
import com.wuest.from_the_depths.davoleo.TotemTextureLoader;
import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import com.wuest.from_the_depths.entityinfo.restrictions.RestrictionBundle;
import com.wuest.from_the_depths.integration.SSHelper;
import com.wuest.from_the_depths.items.ItemTotemOfSpawning;
import com.wuest.from_the_depths.proxy.messages.ConfigSyncMessage;
import com.wuest.from_the_depths.proxy.messages.handlers.ConfigSyncHandler;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;
import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
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
import net.minecraft.util.Tuple;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is the mod registry so there is a way to get to all instances of the
 * blocks/items created by this mod.
 *
 * @author WuestMan
 */
public class ModRegistry {
	/**
	 * The ArrayList of mod registered items.
	 */
	public static ArrayList<Item> ModItems = new ArrayList<>();

	/**
	 * The ArrayList of mod registered blocks.
	 */
	public static ArrayList<Block> ModBlocks = new ArrayList<>();

	/**
	 * THe ArrayList of mod spawn infos.
	 */
	public static ArrayList<SpawnInfo> SpawnInfos = new ArrayList<>();

	/**
	 * The hashmap of mod guis.
	 */
	public static HashMap<Integer, Class<? extends Gui>> ModGuis = new HashMap<>();

	/**
	 * This hashmap links spawn information and item registrations.
	 */
	public static HashMap<String, Tuple<SpawnInfo, ItemTotemOfSpawning>> SpawnInfosAndItems = new HashMap<>();

	/**
	 * A hashmap that links SpawnInfo Strings with Restriction collections
	 */
	public static Map<String, RestrictionBundle> spawnRestrictions = new HashMap<>();

	/**
	 * The identifier for the ChickenCoop GUI.
	 */
	public static final int GuiChickenCoop = 1;

	public static ItemTotemOfSpawning TotemOfSpawning() {
		return ModRegistry.GetItem(ItemTotemOfSpawning.class);
	}

	public static BlockAltarOfSpawning AlterOfSpawning() {
		return ModRegistry.GetBlock(BlockAltarOfSpawning.class);
	}

	/**
	 * Gets the item from the ModItems collections.
	 *
	 * @param <T>          The type which extends item.
	 * @param genericClass The class of item to get from the collection.
	 * @return Null if the item could not be found otherwise the item found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Item> T GetItem(Class<T> genericClass) {
		for (Item entry : ModRegistry.ModItems) {
			if (entry.getClass() == genericClass) {
				return (T) entry;
			}
		}

		return null;
	}

	/**
	 * Gets the block from the ModBlockss collections.
	 *
	 * @param <T>          The type which extends Block.
	 * @param genericClass The class of block to get from the collection.
	 * @return Null if the block could not be found otherwise the block found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Block> T GetBlock(Class<T> genericClass) {
		for (Block entry : ModRegistry.ModBlocks) {
			if (entry.getClass() == genericClass) {
				return (T) entry;
			}
		}

		return null;
	}

	/**
	 * Gets the gui screen for the ID and passes position data to it.
	 *
	 * @param id The ID of the screen to get.
	 * @param x  The X-Axis of where this screen was created from, this is used to
	 *           create a BlockPos.
	 * @param y  The Y-Axis of where this screen was created from, this is used to
	 *           create a BlockPos.
	 * @param z  The Z-Axis of where this screen was created from, this is used to
	 *           create a BlockPos.
	 * @return Null if the screen wasn't found, otherwise the screen found.
	 */
	public static GuiScreen GetModGuiByID(int id, int x, int y, int z) {
		for (Entry<Integer, Class<? extends Gui>> entry : ModRegistry.ModGuis.entrySet()) {
			if (entry.getKey() == id) {
				try {
					return (GuiScreen) entry.getValue().getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	/**
	 * This is where all in-game mod components (Items, Blocks) will be registered.
	 */
	public static void RegisterModComponents() {
		try {
			Block block = new BlockAltarOfSpawning("block_altar_of_summoning");
			ModRegistry.registerBlock(block);
		} catch (Exception ex) {
			FromTheDepths.logger.warn(ex.getMessage());
		}

		ItemTotemOfSpawning baseTotem = new ItemTotemOfSpawning(null, "totem");
		ModRegistry.registerItem(baseTotem);

		GameRegistry.registerTileEntity(TileEntityAltarOfSpawning.class, new ResourceLocation("from_the_depths:block_altar_of_summoning"));

		for (SpawnInfo spawnInfo : ModRegistry.SpawnInfos) {
			ItemTotemOfSpawning registeredItem = new ItemTotemOfSpawning(spawnInfo.key, "totem");
			ModRegistry.registerItem(registeredItem);
			ModRegistry.SpawnInfosAndItems.put(spawnInfo.key, new Tuple<>(spawnInfo, registeredItem));

			//Generate Custom ItemModel
			try {
				TotemTextureLoader.generateItemModels(spawnInfo.key);
			}
			catch (IOException e) {
				FromTheDepths.logger.warn(e.getMessage());
			}
		}
	}

	/**
	 * Registers records into the ore dictionary.
	 */
	public static void RegisterOreDictionaryRecords() {
		// Register certain blocks into the ore dictionary.
	}

	/**
	 * This is where the mod packets are registered.
	 */
	public static void RegisterMessages() {
		FromTheDepths.network.registerMessage(ConfigSyncHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
	}

	/**
	 * This is where mod capabilities are registered.
	 */
	public static void RegisterCapabilities() {
		// Register the dimension home capability.
		// CapabilityManager.INSTANCE.register(IStructureConfigurationCapability.class,
		// new StructureConfigurationStorage(),
		// StructureConfigurationCapability.class);
	}

	/**
	 * This method is used to register spawning information from the mod directory.
	 */
	public static void RegisterSpawningInfo() {
		Gson GSON = new GsonBuilder().create();

		if (FromTheDepths.proxy.modDirectory != null || !FromTheDepths.proxy.modDirectory.toFile().exists()) {
			File[] childFiles = FromTheDepths.proxy.modDirectory.toFile().listFiles();

			if (childFiles != null && childFiles.length > 0) {
				for (File file : childFiles) {
					if (file.isFile()) {
						Path path = file.toPath();

						try {
							String fileContents = Files.toString(file, StandardCharsets.UTF_8);

							ModRegistry.SpawnInfos.add(GSON.fromJson(fileContents, SpawnInfo.class));
						} catch (JsonParseException e) {
							FromTheDepths.logger.error("From_The_Depths: Parsing error loading spawn information: {}. {}",
									path.toString(), e);
						} catch (Exception e) {
							FromTheDepths.logger.error("From_The_Depths: Error loading spawn information {}. {}", path.toString(), e);
						}
					}
				}
			}
		}
	}

	/**
	 * Loads spawn restrictions from json configuration
	 */
	public static void registerSpawnRestrictions() {
		if (FromTheDepths.proxy.modDirectory.toFile().exists()) {
			for (File file : FromTheDepths.proxy.modDirectory.toFile().listFiles()) {
				if (file.isFile()) {
					try {
						Gson gson = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter()).create();

						BufferedReader reader = java.nio.file.Files.newBufferedReader(file.toPath());
						JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class, true);

						if (JsonUtils.hasField(json, "restrictions")) {
							JsonObject restrictions = JsonUtils.getJsonObject(json, "restrictions");

							String key = JsonUtils.getString(json, "key");

							if (SSHelper.isSereneSeasonLoaded.getAsBoolean() && restrictions.has("sereneSeasons")) {
								JsonObject seasonObj = JsonUtils.getJsonObject(restrictions, "sereneSeasons");
								SSHelper.addSeasonRestriction(key, seasonObj);
							}

							RestrictionBundle bundle = gson.fromJson(restrictions, RestrictionBundle.class);

							//FromTheDepths.logger.info("Registering Spawn info restrictions for " + key + ". Restrictions: " + bundle);
							spawnRestrictions.put(key, bundle);
						}
					} catch (IOException | IllegalArgumentException | JsonSyntaxException exception) {
						FromTheDepths.logger.error("From_The_Depths: Error Loading Spawn Restrictions: {}. {}", file.getPath(), exception);
					}
				}
			}
		}
	}

	/**
	 * This method is used to register totem of summoning recipes.
	 */
	public static void RegisterTotemOfSummoningRecipes() {
		if (FromTheDepths.proxy.modDirectory.toFile().exists()) {
			JsonContext ctx = new JsonContext(FromTheDepths.MODID);
			Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
			boolean loadedRecipe = false;

			for (File file : FromTheDepths.proxy.modDirectory.toFile().listFiles()) {
				if (file.isFile()) {
					Path path = file.toPath();
					String name = Files.getNameWithoutExtension(file.getName()).toLowerCase().replace(' ', '_').replace('-', '_')
							.replace('\t', '_');

					ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

					try {
						String fileContents = Files.toString(file, StandardCharsets.UTF_8);

						JsonObject json = JsonUtils.fromJson(GSON, fileContents, JsonObject.class, true);

						if (json.has("recipe")) {
							JsonObject recipeObject = json.getAsJsonObject("recipe");
							String informationKey = json.get("key").getAsString();

							if (ModRegistry.SpawnInfosAndItems.containsKey(informationKey)) {
								// Tuple<SpawnInfo, ItemTotemOfSpawning> registeredLink = ModRegistry.SpawnInfosAndItems.get(informationKey);
								IRecipe recipe = CraftingHelper.getRecipe(recipeObject, ctx);
								ItemStack recipeOutput = recipe.getRecipeOutput();

								// Limit recipe registration to ONLY the items which are totems of spawning.
								if (recipeOutput.getItem() instanceof ItemTotemOfSpawning) {

									// Make sure to save the share tag for later usage.
									recipeOutput.getItem().getNBTShareTag(recipeOutput);

									ForgeRegistries.RECIPES.register(recipe.setRegistryName(key));
									loadedRecipe = true;
								}
							} else {
								FromTheDepths.logger.warn(
										"There is no recipe information for file: {}. This boss will not be able to be spawned unless a recipe is registered through other means.",
										path.toString());
							}
						}
					} catch (JsonParseException e) {
						FromTheDepths.logger.error("From_The_Depths: Parsing error loading recipe {}. {}", key, e);
					} catch (Exception e) {
						FromTheDepths.logger.error("From_The_Depths: Error loading recipe {}. {}", key, e);
					}
				}
			}

			if (loadedRecipe) {
				// A recipe was added during this process; reset the client-side recipe book if
				// it's used.
				FMLCommonHandler.instance().resetClientRecipeBook();
			}
		}
	}

	/**
	 * Register an Item
	 *
	 * @param item The Item instance
	 * @param <T>  The Item type
	 * @return The Item instance
	 */
	public static <T extends Item> T registerItem(T item) {
		ModRegistry.ModItems.add(item);

		return item;
	}

	/**
	 * Registers a block in the game registry.
	 *
	 * @param <T>   The type of block to register.
	 * @param block The block to register.
	 * @return The block which was registered.
	 */
	public static <T extends Block> T registerBlock(T block) {
		return ModRegistry.registerBlock(block, true);
	}

	/**
	 * Registers a block in the game registry.
	 *
	 * @param <T>              The type of block to register.
	 * @param block            The block to register.
	 * @param includeItemBlock True to include a default item block.
	 * @return The block which was registered.
	 */
	public static <T extends Block> T registerBlock(T block, boolean includeItemBlock) {
		if (includeItemBlock) {
			ModItems.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}

		ModRegistry.ModBlocks.add(block);

		return block;
	}

	/**
	 * Registers a block in the game registry.
	 *
	 * @param <T>       The type of block to register.
	 * @param <I>       The type of item block to register.
	 * @param block     The block to register.
	 * @param itemBlock The item block to register with the block.
	 * @return The block which was registered.
	 */
	public static <T extends Block, I extends ItemBlock> T registerBlock(T block, I itemBlock) {
		ModRegistry.ModBlocks.add(block);

		if (itemBlock != null) {
			ModRegistry.ModItems.add(itemBlock);
		}

		return block;
	}

	/**
	 * Set the registry name of {@code item} to {@code itemName} and the
	 * un-localised name to the full registry name.
	 *
	 * @param item     The item
	 * @param itemName The item's name
	 */
	public static void setItemName(Item item, String itemName) {
		if (itemName != null) {
			item.setRegistryName("from_the_depths:" + itemName);
			item.setTranslationKey(item.getRegistryName().toString());
		}
	}

	/**
	 * Set the registry name of {@code block} to {@code blockName} and the
	 * un-localised name to the full registry name.
	 *
	 * @param block     The block
	 * @param blockName The block's name
	 */
	public static void setBlockName(Block block, String blockName) {
		block.setRegistryName(blockName);
		block.setTranslationKey(block.getRegistryName().toString());
	}

	/**
	 * Adds all of the Mod Guis to the HasMap.
	 */
	public static void AddGuis() {
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and not
	 * mod-specific objects.
	 *
	 * @param name             The name of the recipe. ModID is pre-pended to it.
	 * @param stack            The output of the recipe.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapedRecipes AddShapedRecipe(String name, String groupName, ItemStack stack,
												Object... recipeComponents) {
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;

		ShapedPrimer primer = CraftingHelper.parseShaped(recipeComponents);
		ShapedRecipes shapedrecipes = new ShapedRecipes(groupName, primer.width, primer.height, primer.input, stack);
		shapedrecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapedrecipes);

		return shapedrecipes;
	}

	/**
	 * This should only be used for registering recipes for vanilla objects and not
	 * mod-specific objects.
	 *
	 * @param name             The name of the recipe.
	 * @param stack            The output stack.
	 * @param recipeComponents The recipe components.
	 */
	public static ShapelessRecipes AddShapelessRecipe(String name, String groupName, ItemStack stack,
													  Object... recipeComponents) {
		name = FromTheDepths.MODID.toLowerCase().replace(' ', '_') + ":" + name;
		NonNullList<Ingredient> list = NonNullList.create();

		for (Object object : recipeComponents) {
			if (object instanceof ItemStack) {
				list.add(Ingredient.fromStacks(((ItemStack) object).copy()));
			} else if (object instanceof Item) {
				list.add(Ingredient.fromStacks(new ItemStack((Item) object)));
			} else {
				if (!(object instanceof Block)) {
					throw new IllegalArgumentException(
							"Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
				}

				list.add(Ingredient.fromStacks(new ItemStack((Block) object)));
			}
		}

		ShapelessRecipes shapelessRecipes = new ShapelessRecipes(groupName, stack, list);
		shapelessRecipes.setRegistryName(name);
		ForgeRegistries.RECIPES.register(shapelessRecipes);

		return shapelessRecipes;
	}
}