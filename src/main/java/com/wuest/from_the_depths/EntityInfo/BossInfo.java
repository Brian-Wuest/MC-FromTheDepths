package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.nbt.NBTTagCompound;

public class BossInfo extends BaseMonster implements INBTSerializable<BossInfo> {

  public BossInfo() {
    super();
  }

  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);
  }

  public BossInfo loadFromNBTData(NBTTagCompound tag) {
    super.loadFromNBT(tag);

    return this;
  }

  @Override
  public BossInfo clone() {
    BossInfo newInstance = new BossInfo();

    newInstance.alwaysShowDisplayName = this.alwaysShowDisplayName;
    newInstance.attackDamage = this.attackDamage;
    newInstance.displayName = this.displayName;
    newInstance.domain = this.domain;
    newInstance.maxHealth = this.maxHealth;
    newInstance.name = this.name;

    return newInstance;
  }
}