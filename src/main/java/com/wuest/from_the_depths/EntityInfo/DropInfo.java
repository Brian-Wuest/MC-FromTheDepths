package com.wuest.from_the_depths.EntityInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wuest.from_the_depths.FromTheDepths;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DropInfo implements INBTSerializable<DropInfo> {
	public String item;
	public int minDrops;
	public int maxDrops;
	public int dropChance;
	public int data;
	public JsonObject nbt;

	public DropInfo() {
		super();
		this.item = "";
		this.minDrops = 0;
		this.maxDrops = 0;
		this.dropChance = 5;
		this.data = 0;
		this.nbt = null;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("item", this.item);
		tag.setInteger("minDrops", this.minDrops);
		tag.setInteger("maxDrops", this.maxDrops);
		tag.setInteger("dropChance", this.dropChance);
		tag.setInteger("data", this.data);

		if (this.nbt != null) {
			tag.setString("nbt", this.nbt.toString());
		}
	}

	@Override
	public DropInfo loadFromNBTData(NBTTagCompound nbtData) {
		this.item = nbtData.getString("item");
		this.minDrops = nbtData.getInteger("minDrops");
		this.maxDrops = nbtData.getInteger("maxDrops");
		this.dropChance = nbtData.getInteger("dropChance");
		this.data = nbtData.getInteger("data");

		if (nbtData.hasKey("nbt")) {
			JsonParser parser = new JsonParser();
			this.nbt = (JsonObject) parser.parse(nbtData.getString("nbt"));
		}

		if (this.minDrops < 0) {
			this.minDrops = 0;
			FromTheDepths.logger.warn("Minimum Drops value is less than zero; please check your files. Setting to zero.");
		}

		if (this.maxDrops < 0) {
			this.maxDrops = 0;
			FromTheDepths.logger.warn("Maximum Drops value is less than zero; please check your files. Setting to zero.");
		}

		if (this.maxDrops < this.minDrops) {
			this.maxDrops = this.minDrops;
			FromTheDepths.logger
					.warn("Maximum Drops value is less than minimum drops; please check your files. Setting to minimum drops.");
		}

		if (this.dropChance < 0) {
			this.dropChance = 5;
			FromTheDepths.logger.warn("The drop chance is less than zero; please check yoru files. Setting to five.");
		}

		if (this.data < 0) {
			this.data = 0;
		}

		return this;
	}

	@Override
	public DropInfo clone() {
		DropInfo newInstance = new DropInfo();

		newInstance.item = this.item;
		newInstance.minDrops = this.minDrops;
		newInstance.maxDrops = this.maxDrops;
		newInstance.dropChance = this.dropChance;
		newInstance.data = this.data;
		newInstance.nbt = this.nbt;

		return newInstance;
	}

	public EntityItem createEntityItem(World world, BlockPos pos) {
		ResourceLocation itemLocation = new ResourceLocation(this.item);
		int randomValue = BaseMonster.determineRandomInt(100, world);

		if (randomValue <= this.dropChance && this.maxDrops > 0) {
			// This drop will be created; determine how many to put into a stack.
			int amountToDrop;

			if (this.minDrops == this.maxDrops) {
				amountToDrop = this.maxDrops;
			} else {
				amountToDrop = BaseMonster.determineRandomInt(this.maxDrops, world);

				if (amountToDrop <= this.minDrops) {
					amountToDrop = this.minDrops;
				}
			}

			try {
				Item registryItem = Item.REGISTRY.getObject(itemLocation);

				if (registryItem != null) {
					ItemStack stack = new ItemStack(registryItem, amountToDrop, data);

					if (this.nbt != null) {
						NBTTagCompound compound = null;
						try {
							compound = JsonToNBT.getTagFromJson(this.nbt.toString());
						} catch (NBTException exception) {
							FromTheDepths.logger.error(exception);
						}

						if (compound != null && !compound.isEmpty()) {
						  stack.setTagCompound(compound);
						}
					}

					return new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			} catch (Exception ex) {
				FromTheDepths.logger.warn(String.format(
						"An item with registration name [{}] wasn't found. Make sure the domain and item name are spelled correctly. Monster drops not created.",
						this.item));
			}
		}

		return null;
	}
}