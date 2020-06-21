package com.wuest.from_the_depths.Items;

import com.wuest.from_the_depths.EntityInfo.SpawnInfo;
import com.wuest.from_the_depths.ModRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.Stack;

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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		SpawnInfo info = this.getSpawnInfoFromItemStack(heldStack);

		if (info != null) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, heldStack);
		}
		else {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, heldStack);
		}
	}

	public SpawnInfo getSpawnInfoFromItemStack(ItemStack stack) {
		NBTTagCompound tagCompound = this.getNBTShareTag(stack);
		String spawn_info = tagCompound.getString("spawn_info");

		if (spawn_info == null || spawn_info.isEmpty()) {
			spawn_info = ((ItemTotemOfSpawning)stack.getItem()).key;
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