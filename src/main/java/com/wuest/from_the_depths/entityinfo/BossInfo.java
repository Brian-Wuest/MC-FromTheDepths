package com.wuest.from_the_depths.entityinfo;

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
    newInstance.timeToWaitBeforeSpawn = this.timeToWaitBeforeSpawn;
    newInstance.commandToRunAtSpawn = this.commandToRunAtSpawn;
    newInstance.nbt = this.nbt;
    newInstance.spawnEffect = this.spawnEffect;
    newInstance.shouldSpawnInAir = this.shouldSpawnInAir;
    newInstance.warningMessage = this.warningMessage;
    newInstance.spawnedMessage = this.spawnedMessage;

    for (DropInfo info : this.additionalDrops) {
      newInstance.additionalDrops.add(info.clone());
    }

    return newInstance;
  }
}