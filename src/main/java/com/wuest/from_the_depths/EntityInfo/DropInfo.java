package com.wuest.from_the_depths.EntityInfo;

import com.wuest.from_the_depths.FromTheDepths;

import net.minecraft.nbt.NBTTagCompound;

public class DropInfo implements INBTSerializable<DropInfo> {

  public String item;
  public int minDrops;
  public int maxDrops;
  public int dropChance;

  public DropInfo() {
    super();
    this.item = "";
    this.minDrops = 0;
    this.maxDrops = 0;
    this.dropChance = 5;
  }

  @Override
  public void writeToNBT(NBTTagCompound tag) {
    tag.setString("item", this.item);
    tag.setInteger("minDrops", this.minDrops);
    tag.setInteger("maxDrops", this.maxDrops);
    tag.setInteger("dropChance", this.dropChance);
  }

  @Override
  public DropInfo loadFromNBTData(NBTTagCompound nbtData) {
    this.item = nbtData.getString("item");
    this.minDrops = nbtData.getInteger("minDrops");
    this.maxDrops = nbtData.getInteger("maxDrops");
    this.dropChance = nbtData.getInteger("dropChance");

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

    return this;
  }

  @Override
  public DropInfo clone() {
    DropInfo newInstance = new DropInfo();

    newInstance.item = this.item;
    newInstance.minDrops = this.minDrops;
    newInstance.maxDrops = this.maxDrops;
    newInstance.dropChance = this.dropChance;

    return newInstance;
  }

}