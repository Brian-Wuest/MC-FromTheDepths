package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.nbt.NBTTagCompound;

public class BossAddInfo extends BaseMonster implements INBTSerializable<BossAddInfo> {
  public int spawnFrequency;
  public int totalSpawnDuration;

  public BossAddInfo() {
    super();
  }

  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);

    tag.setInteger("spawnFrequency", this.spawnFrequency);
    tag.setInteger("totalSpawnDuration", this.totalSpawnDuration);
  }

  public BossAddInfo loadFromNBTData(NBTTagCompound tag) {
    super.loadFromNBT(tag);

    if (tag.hasKey("spawnFrequency")) {
      this.spawnFrequency = tag.getInteger("spawnFrequency");
    }

    if (tag.hasKey("totalSpawnDuration")) {
      this.totalSpawnDuration = tag.getInteger("totalSpawnDuration");
    }

    return this;
  }

  @Override
  public BossAddInfo clone() {
    BossAddInfo newInstance = new BossAddInfo();

    newInstance.alwaysShowDisplayName = this.alwaysShowDisplayName;
    newInstance.attackDamage = this.attackDamage;
    newInstance.displayName = this.displayName;
    newInstance.domain = this.domain;
    newInstance.maxHealth = this.maxHealth;
    newInstance.name = this.name;
    newInstance.spawnFrequency = this.spawnFrequency;
    newInstance.totalSpawnDuration = this.totalSpawnDuration;

    return newInstance;
  }
}