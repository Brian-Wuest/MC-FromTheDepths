package com.wuest.from_the_depths.Items;

import javax.annotation.Nullable;

import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.EntityInfo.SpawnInfo;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.translation.I18n;

/**
 * 
 * @author WuestMan
 *
 */
public class ItemTotemOfSpawning extends Item {
  public SpawnInfo spawnInfo;

  public ItemTotemOfSpawning(SpawnInfo spawnInfo, String name) {
    super();

    this.spawnInfo = spawnInfo;

    String registryName = name + "_"
        + spawnInfo.key.toLowerCase().trim().replace(' ', '_').replace('-', '_').replace('\t', '_');

    this.setCreativeTab(CreativeTabs.MATERIALS);
    ModRegistry.setItemName(this, registryName);

    this.setUnlocalizedName(name);
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

    if (!stack.getTagCompound().hasKey("spawn_info") && this.spawnInfo != null) {
      stack.getTagCompound().setString("spawn_info", this.spawnInfo.key);
    }

    return stack.getTagCompound();
  }

  /**
   * Override this method to decide what to do with the NBT data received from
   * getNBTShareTag().
   * 
   * @param stack The stack that received NBT
   * @param nbt   Received NBT, can be null
   */
  @Override
  public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
    super.readNBTShareTag(stack, nbt);

    // If there is a spawn information key; make sure to get it from the registry.
    String spawn_info = nbt.getString("spawn_info");

    if (ModRegistry.SpawnInfosAndItems.containsKey(spawn_info)) {
      Tuple<SpawnInfo, ItemTotemOfSpawning> tuple = ModRegistry.SpawnInfosAndItems.get(spawn_info);
      this.spawnInfo = tuple.getFirst();
    }
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    if (this.spawnInfo != null) {
      String value = ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim() + " ("
          + spawnInfo.key + ")";
      return value;
    }

    return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim();
  }
}