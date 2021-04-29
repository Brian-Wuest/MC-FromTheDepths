package com.wuest.from_the_depths.items;

import com.google.common.base.Strings;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Utilities;
import com.wuest.from_the_depths.base.Triple;
import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/**
 * @author WuestMan
 */
public class ItemTotemOfSpawning extends Item {
	public String key = "";

	public ItemTotemOfSpawning(String key, String name) {
		super();

		this.key = key;
		this.setCreativeTab(CreativeTabs.MATERIALS);

		if (key != null && !key.isEmpty()) {
			String registryName = name + "_"
					+ key.toLowerCase().trim().replace(' ', '_').replace('-', '_').replace('\t', '_');
			ModRegistry.setItemName(this, registryName);
		} else {
			ModRegistry.setItemName(this, name);
		}

		this.setUnlocalizedName("from_the_depths:" + name);
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
		if (stack.getTagCompound() == null || stack.getTagCompound().hasNoTags()) {
			// Make sure to serialize the NBT for this stack so the information is pushed to
			// the client and the appropriate Icon is displayed for this stack.
			stack.setTagCompound(stack.serializeNBT());
		}

		return stack.getTagCompound();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		SpawnInfo spawnInfo = this.getSpawnInfoFromItemStack(stack);

		if (spawnInfo != null) {
			String value = ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim() + " ("
					+ spawnInfo.key + ")";
			return value;
		}

		return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim() + " (Does Nothing. Do not use.)";
	}

	/**
	 * Does something when the item is right-clicked.
	 */
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);

				if (tileEntity instanceof TileEntityAltarOfSpawning) {
					TileEntityAltarOfSpawning tileEntityAltarOfSpawning = (TileEntityAltarOfSpawning) tileEntity;
					ItemStack heldStack = player.getHeldItem(hand);

					if (tileEntityAltarOfSpawning.getConfig().currentSpawnInfo != null
							&& !Strings.isNullOrEmpty(tileEntityAltarOfSpawning.getConfig().currentSpawnInfo.key)) {
						player.sendMessage(new TextComponentString(
								"Cannot spawn a monster at this time as additional monsters are going to be spawned. Please wait for all minions to be spawned."));
					} else {
						// Make sure that there is enough clear space around the altar for spawning.
						Triple<Boolean, BlockPos, BlockPos> result = Utilities.isSpaceAroundAltarAir(pos, worldIn);

						if (!result.getFirst()) {
							TextComponentString message = new TextComponentString(
									"Cannot summon monster. The area around the altar must only be air from Block Position ["
											+ result.getSecond().toString() + "] to Block Position [" + result.getThird()
											+ "]");

							player.sendMessage(message);
							return EnumActionResult.FAIL;
						}

						result = Utilities.isGroundUnderAltarSolid(pos, worldIn);

						if (!result.getFirst()) {
							TextComponentString message = new TextComponentString(
									"Cannot summon monster. The ground around the altar must be solid blocks from Block Position ["
											+ result.getSecond().toString() + "] to Block Position [" + result.getThird()
											+ "]");

							player.sendMessage(message);
							return EnumActionResult.FAIL;
						}

						// Found a totem of spawning and we are not currently spawning a previous set of
						// monsters. Initiate the spawning of the entity.
						ItemTotemOfSpawning totemOfSpawning = (ItemTotemOfSpawning) heldStack.getItem();
						SpawnInfo spawnInfo = totemOfSpawning.getSpawnInfoFromItemStack(heldStack);

						if (spawnInfo != null) {
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
							} else {
								player.sendMessage(new TextComponentString("Entity with name of [" + spawnInfo.bossInfo.name
										+ "] and mod of [" + spawnInfo.bossInfo.domain + "] was not found."));
							}
						}
					}
				}
			} else {
				player.sendMessage(new TextComponentString("The current world difficulty is set to Peaceful. Unable to summon boss or minions."));
			}
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		SpawnInfo info = this.getSpawnInfoFromItemStack(heldStack);

		if (info != null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, heldStack);
		} else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, heldStack);
		}
	}

	public SpawnInfo getSpawnInfoFromItemStack(ItemStack stack) {
		NBTTagCompound tagCompound = this.getNBTShareTag(stack);
		String spawn_info = tagCompound.getString("spawn_info");

		if (spawn_info == null || spawn_info.isEmpty()) {
			spawn_info = ((ItemTotemOfSpawning) stack.getItem()).key;
		}

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