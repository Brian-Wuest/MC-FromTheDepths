package com.wuest.from_the_depths.EntityInfo;

import com.wuest.from_the_depths.FromTheDepths;

import net.minecraft.nbt.NBTTagCompound;

public class BossAddInfo extends BaseMonster implements INBTSerializable<BossAddInfo> {
  public int minSpawns;
  public int maxSpawns;
  public int timeBetweenSpawns;
  public int waitTicksAfterBossSpawn;
  public boolean spawnBeforeBoss;
  public BossAddInfo nextWaveofAdds;

  // spawn tracking fields.
  public boolean spawning;
  public int numberLeftToSpawn;
  public int timeUntilNextSpawn;

  public BossAddInfo() {
    super();

    this.minSpawns = 0;
    this.maxSpawns = 0;
    this.timeBetweenSpawns = 100;
    this.waitTicksAfterBossSpawn = 200;
    this.spawnBeforeBoss = false;
    this.timeBetweenSpawns = 20;

    this.spawning = false;
    this.numberLeftToSpawn = 0;
    this.timeUntilNextSpawn = 0;
  }

  /**
   * Determines if this minion spawns additional waves of minions.
   * 
   * @return True if there is another wave or false if there isn't
   */
  public boolean hasAdditionalWaves() {
    return this.nextWaveofAdds != null;
  }

  public void writeToNBT(NBTTagCompound tag) {
    super.writeToNBT(tag);

    tag.setInteger("minSpawns", this.minSpawns);
    tag.setInteger("maxSpawns", this.maxSpawns);
    tag.setInteger("timeBetweenSpawns", this.timeBetweenSpawns);
    tag.setInteger("waitTicksAfterBossSpawn", this.waitTicksAfterBossSpawn);
    tag.setBoolean("spawnBeforeBoss", this.spawnBeforeBoss);

    if (this.nextWaveofAdds != null) {
      NBTTagCompound nextWaveTag = new NBTTagCompound();

      this.nextWaveofAdds.writeToNBT(nextWaveTag);
      tag.setTag("nextWaveOfAdds", nextWaveTag);
    }
  }

  public BossAddInfo loadFromNBTData(NBTTagCompound tag) {
    super.loadFromNBT(tag);

    this.minSpawns = tag.getInteger("minSpawns");

    if (this.minSpawns < 0) {
      FromTheDepths.logger.warn(
          String.format("The mininum number of spawns (%1) for monster %2 is not valid, setting to default of zero",
              this.minSpawns, this.name));
    }

    this.maxSpawns = tag.getInteger("maxSpawns");

    if (this.maxSpawns < 0 || this.maxSpawns < this.minSpawns) {
      FromTheDepths.logger.warn(String.format(
          "The maximum number of spawns (%1) for monster %2 is not valid, setting to minimum number of spawns (%3_",
          this.maxSpawns, this.maxHealth, this.minSpawns));
    }

    this.timeBetweenSpawns = tag.getInteger("timeBetweenSpawns");

    if (this.timeBetweenSpawns < 0) {
      FromTheDepths.logger.warn(
          String.format("The time between spawns (%1) for monster %2 is not valid, setting to default of 100 ticks",
              this.timeBetweenSpawns, this.name));
      this.timeBetweenSpawns = 100;
    }

    this.waitTicksAfterBossSpawn = tag.getInteger("waitTicksAfterBossSpawn");

    if (this.waitTicksAfterBossSpawn < 0) {
      FromTheDepths.logger.warn(String.format(
          "The number of ticks to wait to spawn adds after boss spawn (%1) is not valid for monster %2 setting to default value of 200"),
          this.waitTicksAfterBossSpawn, this.name);
    }

    this.spawnBeforeBoss = tag.getBoolean("spawnBeforeBoss");

    if (tag.hasKey("nextWaveOfAdds")) {
      NBTTagCompound nextWaveTag = tag.getCompoundTag("nextWaveOfAdds");
      BossAddInfo nextWaveAddInfo = new BossAddInfo();
      this.nextWaveofAdds = nextWaveAddInfo.loadFromNBTData(nextWaveTag);
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
    newInstance.minSpawns = this.minSpawns;
    newInstance.maxSpawns = this.maxSpawns;
    newInstance.timeBetweenSpawns = this.timeBetweenSpawns;
    newInstance.waitTicksAfterBossSpawn = this.waitTicksAfterBossSpawn;
    newInstance.spawnBeforeBoss = this.spawnBeforeBoss;
    newInstance.timeToWaitBeforeSpawn = this.timeToWaitBeforeSpawn;

    if (this.nextWaveofAdds != null) {
      newInstance.nextWaveofAdds = this.nextWaveofAdds.clone();
    }

    for (DropInfo info : this.additionalDrops) {
      newInstance.additionalDrops.add(info.clone());
    }

    return newInstance;
  }
}