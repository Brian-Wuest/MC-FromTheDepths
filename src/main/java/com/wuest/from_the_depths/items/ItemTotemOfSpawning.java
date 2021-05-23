package com.wuest.from_the_depths.items;

import com.google.common.base.Strings;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Utilities;
import com.wuest.from_the_depths.base.Triple;
import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import com.wuest.from_the_depths.entityinfo.restrictions.RestrictionBundle;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author WuestMan
 */
public class ItemTotemOfSpawning extends Item {
	public String key;

	public ItemTotemOfSpawning(String key, String name) {
		super();

		this.key = key;
		this.setCreativeTab(FromTheDepths.CREATIVE_TAB);

		if (key != null && !key.isEmpty()) {
			String registryName = name + "_"
					+ key.toLowerCase().trim().replace(' ', '_').replace('-', '_').replace('\t', '_');
			ModRegistry.setItemName(this, registryName);
		} else {
			ModRegistry.setItemName(this, name);
		}

		this.setTranslationKey("from_the_depths:" + name);
	}

	/**
	 * Override this method to change the NBT data being sent to the client. You
	 * should ONLY override this when you have no other choice, as this might change
	 * behavior client side!
	 *
	 * @param stack The stack to send the NBT tag for
	 * @return The NBT tag
	 */
	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		if (stack.getTagCompound() == null || stack.getTagCompound().isEmpty()) {
			// Make sure to serialize the NBT for this stack so the information is pushed to
			// the client and the appropriate Icon is displayed for this stack.
			stack.setTagCompound(stack.serializeNBT());
		}

		return stack.getTagCompound();
	}

	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		SpawnInfo spawnInfo = this.getSpawnInfoFromItemStack(stack);

		if (spawnInfo != null) {
			//All non-word characters + the underscore
			String[] splitKey = spawnInfo.key.split("\\W|_");
			String processedKey = Arrays.stream(splitKey).map(Utilities::capitalize).collect(Collectors.joining(" "));
			return Utilities.localize(stack.getTranslationKey()).trim() + " (" + processedKey + ")";
		}

		return Utilities.localize(stack.getTranslationKey()).trim() + " (" + Utilities.localize("from_the_depths.messages.no_boss") + ")";
	}

	/**
	 * Does something when the item is right-clicked.
	 */
	@Nonnull
	@Override
	public EnumActionResult onItemUse(@Nonnull EntityPlayer player, World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);

				if (tileEntity instanceof TileEntityAltarOfSpawning) {
					TileEntityAltarOfSpawning tileEntityAltarOfSpawning = (TileEntityAltarOfSpawning) tileEntity;
					ItemStack heldStack = player.getHeldItem(hand);

					if (tileEntityAltarOfSpawning.getConfig().currentSpawnInfo != null
							&& !Strings.isNullOrEmpty(tileEntityAltarOfSpawning.getConfig().currentSpawnInfo.key)) {
						player.sendMessage(new TextComponentTranslation("from_the_depths.messages.early_summon"));
					} else {

						if (FromTheDepths.proxy.getServerConfiguration().enableArenaStyleRestrictions) {
							// Make sure that there is enough clear space around the altar for spawning.
							Triple<Boolean, BlockPos, BlockPos> result = Utilities.isSpaceAroundAltarAir(pos, worldIn);

							if (!result.getFirst()) {
								TextComponentTranslation message = new TextComponentTranslation(
										"from_the_depths.messages.invalid_arena",
										FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius,
										FromTheDepths.proxy.getServerConfiguration().altarSpawningHeight
								);
								player.sendMessage(message);
								return EnumActionResult.FAIL;
							}

							result = Utilities.isGroundUnderAltarSolid(pos, worldIn, FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius);

							if (!result.getFirst()) {
								TextComponentTranslation message = new TextComponentTranslation(
										"from_the_depths.messages.invalid_arena_ground",
										FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius
								);
								player.sendMessage(message);
								return EnumActionResult.FAIL;
							}
						}

						// Found a totem of spawning and we are not currently spawning a previous set of
						// monsters. Initiate the spawning of the entity.
						ItemTotemOfSpawning totemOfSpawning = (ItemTotemOfSpawning) heldStack.getItem();
						SpawnInfo spawnInfo = totemOfSpawning.getSpawnInfoFromItemStack(heldStack);

						if (spawnInfo != null) {
							RestrictionBundle restrictionBundle = ModRegistry.spawnRestrictions.get(spawnInfo.key);
							if (restrictionBundle != null) {
								Pair<Boolean, TextComponentTranslation> testResults = restrictionBundle.testAll(spawnInfo.key, worldIn, pos);
								if (!testResults.getLeft()) {
									player.sendMessage(testResults.getRight());
									return EnumActionResult.FAIL;
								}
							}

							if (spawnInfo.bossInfo.isValidEntity(worldIn)) {
								// Entity was spawned, update the itemstack.
								if (heldStack.getCount() == 1) {
									player.inventory.deleteStack(heldStack);
								} else {
									heldStack.shrink(1);
								}

								player.inventoryContainer.detectAndSendChanges();

								// Save off the spawn information for this tile entity since adds need to be
								// spawned.
								// Make sure to clone the spawn information before initiate spawning.
								tileEntityAltarOfSpawning.InitiateSpawning(spawnInfo.clone(), 20, worldIn);

								return EnumActionResult.SUCCESS;
							} else
								player.sendMessage(new TextComponentTranslation("from_the_depths.messages.boss_entity_not_found", spawnInfo.bossInfo.name, spawnInfo.bossInfo.domain));
						}
					}
				}
			} else {
				player.sendMessage(new TextComponentTranslation("from_the_depths.messages.peaceful_mode"));
			}
		}

		return EnumActionResult.FAIL;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		SpawnInfo info = this.getSpawnInfoFromItemStack(heldStack);

		if (info != null)
			return new ActionResult<>(EnumActionResult.PASS, heldStack);
		else
			return new ActionResult<>(EnumActionResult.FAIL, heldStack);
	}

	public SpawnInfo getSpawnInfoFromItemStack(ItemStack stack) {
		NBTTagCompound tagCompound = this.getNBTShareTag(stack);
		String spawn_info = tagCompound.getString("spawn_info");

		if (spawn_info == null || spawn_info.isEmpty())
			spawn_info = ((ItemTotemOfSpawning) stack.getItem()).key;

		return this.getSpawnInfoFromKey(spawn_info);
	}

	public SpawnInfo getSpawnInfoFromKey(String key) {
		if (ModRegistry.SpawnInfosAndItems.containsKey(key)) {
			Tuple<SpawnInfo, ItemTotemOfSpawning> tuple = ModRegistry.SpawnInfosAndItems.get(key);
			return tuple.getFirst();
		}

		return null;
	}
}