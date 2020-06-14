package com.wuest.from_the_depths.EntityInfo;

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
    this.dropChance = 0;
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
    if (nbtData.hasKey("item")) {
      this.item = nbtData.getString("item");
    }

    if (nbtData.hasKey("minDrops")) {
      this.minDrops = nbtData.getInteger("minDrops");
    }

    if (nbtData.hasKey("maxDrops")) {
      this.maxDrops = nbtData.getInteger("maxDrops");
    }

    if (nbtData.hasKey("dropChance")) {
      this.dropChance = nbtData.getInteger("dropChance");
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