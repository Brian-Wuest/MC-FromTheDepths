package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.nbt.NBTTagCompound;

public class SpawnInfo implements INBTSerializable<SpawnInfo> {
  public String key;
  public BossInfo bossInfo;
  public BossAddInfo bossAddInfo;

  public SpawnInfo() {
  }

  public SpawnInfo(BossInfo bossInfo) {
    this();
    this.bossInfo = bossInfo;
  }

  public SpawnInfo(BossInfo bossInfo, BossAddInfo bossAddInfo) {
    this(bossInfo);
    this.bossAddInfo = bossAddInfo;
  }

  public SpawnInfo clone() {
    SpawnInfo newInstance = new SpawnInfo();

    newInstance.key = this.key;

    newInstance.bossInfo = new BossInfo();
    newInstance.bossAddInfo = new BossAddInfo();

    if (this.bossInfo != null) {
      newInstance.bossInfo = this.bossInfo.clone();
    }

    if (this.bossAddInfo != null) {
      newInstance.bossAddInfo = this.bossAddInfo.clone();
    }

    return newInstance;
  }

  public void writeToNBT(NBTTagCompound tag) {
    tag.setString("key", this.key);
    NBTTagCompound bossInfoTag = new NBTTagCompound();
    NBTTagCompound bossAddInfoTag = new NBTTagCompound();

    this.bossInfo.writeToNBT(bossInfoTag);
    tag.setTag("bossInfo", bossInfoTag);

    if (this.bossAddInfo != null) {
      this.bossAddInfo.writeToNBT(bossAddInfoTag);
      tag.setTag("bossAddInfo", bossAddInfoTag);
    }
  }

  public SpawnInfo loadFromNBTData(NBTTagCompound nbtData) {
    SpawnInfo spawnInfo = new SpawnInfo();

    if (nbtData.hasKey("key")) {
      spawnInfo.key = nbtData.getString("key");
    }

    if (nbtData.hasKey("bossInfo")) {
      NBTTagCompound bossCompound = nbtData.getCompoundTag("bossInfo");
      BossInfo bossInfo = new BossInfo();
      spawnInfo.bossInfo = bossInfo.loadFromNBTData(bossCompound);
    }

    if (nbtData.hasKey("bossAddInfo")) {
      NBTTagCompound bossAddInfo = nbtData.getCompoundTag("bossAddInfo");
      BossAddInfo addInfo = new BossAddInfo();
      spawnInfo.bossAddInfo = addInfo.loadFromNBTData(bossAddInfo);
    }

    return spawnInfo;
  }
}