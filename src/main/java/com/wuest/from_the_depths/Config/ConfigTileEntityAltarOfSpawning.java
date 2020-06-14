package com.wuest.from_the_depths.Config;

import com.google.common.base.Strings;
import com.wuest.from_the_depths.Base.BaseConfig;
import com.wuest.from_the_depths.EntityInfo.SpawnInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

/**
 * This is the configuration class for the drafter tile entity. This is what
 * will be saved to NBTTag data.
 * 
 * @author WuestMan
 *
 */
public class ConfigTileEntityAltarOfSpawning extends BaseConfig {
  public BlockPos pos;
  public SpawnInfo currentSpawnInfo;
  public int ticksForCurrentSpawn;
  public boolean spawningBoss;
  public int totalLightningBolts;
  public int ticksUntilNextLightningBolt;

  public ConfigTileEntityAltarOfSpawning() {
    super();
    this.Initialize();
  }

  @Override
  public void WriteToNBTCompound(NBTTagCompound compound) {
    NBTTagCompound tag = new NBTTagCompound();

    tag.setTag("pos", NBTUtil.createPosTag(this.pos));

    if (this.currentSpawnInfo != null && this.currentSpawnInfo.bossInfo != null
        && !Strings.isNullOrEmpty(this.currentSpawnInfo.key)) {
      NBTTagCompound spawnInfo = new NBTTagCompound();
      this.currentSpawnInfo.writeToNBT(spawnInfo);
      tag.setTag("spawnInfo", spawnInfo);

      tag.setInteger("ticksForCurrentSpawn", this.ticksForCurrentSpawn);
      tag.setBoolean("spawningBoss", this.spawningBoss);
      tag.setInteger("totalLightningBolts", this.totalLightningBolts);
      tag.setInteger("ticksUntilNextLightningBolt", this.ticksUntilNextLightningBolt);
    }

    compound.setTag("configTag", tag);
  }

  @Override
  public ConfigTileEntityAltarOfSpawning ReadFromNBTTagCompound(NBTTagCompound compound) {
    ConfigTileEntityAltarOfSpawning config = new ConfigTileEntityAltarOfSpawning();

    if (compound.hasKey("configTag")) {
      NBTTagCompound tag = compound.getCompoundTag("configTag");

      if (tag.hasKey("pos")) {
        config.pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));
      }

      if (tag.hasKey("spawnInfo")) {
        SpawnInfo spawnInfo = new SpawnInfo();
        config.currentSpawnInfo = spawnInfo.loadFromNBTData(tag.getCompoundTag("spawnInfo"));

        if (Strings.isNullOrEmpty(config.currentSpawnInfo.key)) {
          config.currentSpawnInfo = null;
        }

        if (tag.hasKey("ticksForCurrentSpawn")) {
          config.ticksForCurrentSpawn = tag.getInteger("ticksForCurrentSpawn");
        }

        if (tag.hasKey("spawningBoss")) {
          config.spawningBoss = tag.getBoolean("spawningBoss");

          if (tag.hasKey("totalLightningBolts")) {
            config.totalLightningBolts = tag.getInteger("totalLightningBolts");
          }

          if (tag.hasKey("ticksUntilNextLightningBolt")) {
            config.ticksUntilNextLightningBolt = tag.getInteger("ticksUntilNextLightningBolt");
          }
        }
      }
    }

    return config;
  }

  public void Initialize() {
    this.pos = new BlockPos(0, 0, 0);
    this.currentSpawnInfo = null;
  }
}