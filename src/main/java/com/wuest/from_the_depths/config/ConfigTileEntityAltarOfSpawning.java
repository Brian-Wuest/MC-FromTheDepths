package com.wuest.from_the_depths.config;

import com.google.common.base.Strings;
import com.wuest.from_the_depths.base.BaseConfig;
import com.wuest.from_the_depths.entityinfo.BossAddInfo;
import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.UUID;

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
  public boolean bossSpawned;
  public int totalLightningBolts;
  public int ticksUntilNextLightningBolt;
  public ArrayList<BossAddInfo> preBossMinions;
  public ArrayList<UUID> aliveMonsterIds;
  public boolean idle;

  public ConfigTileEntityAltarOfSpawning() {
    super();
    this.Initialize();
  }

  @Override
  public void writeToNBTCompound(NBTTagCompound compound) {
    NBTTagCompound tag = new NBTTagCompound();

    tag.setTag("pos", NBTUtil.createPosTag(this.pos));

    if (this.currentSpawnInfo != null && this.currentSpawnInfo.bossInfo != null
        && !Strings.isNullOrEmpty(this.currentSpawnInfo.key)) {
      NBTTagCompound spawnInfo = new NBTTagCompound();
      this.currentSpawnInfo.writeToNBT(spawnInfo);
      tag.setTag("spawnInfo", spawnInfo);

      tag.setBoolean("bossSpawned", this.bossSpawned);
      tag.setInteger("totalLightningBolts", this.totalLightningBolts);
      tag.setInteger("ticksUntilNextLightningBolt", this.ticksUntilNextLightningBolt);

      NBTTagList tagList = new NBTTagList();
      if (!this.preBossMinions.isEmpty()) {
        for (BossAddInfo bossAddInfo : this.preBossMinions) {
          NBTTagCompound addTagCompound = new NBTTagCompound();
          bossAddInfo.writeToNBT(addTagCompound);
          tagList.appendTag(addTagCompound);
        }
      }

      tag.setTag("preBossMinions", tagList);

      NBTTagList idList = new NBTTagList();
      this.aliveMonsterIds.forEach(uuid -> idList.appendTag(NBTUtil.createUUIDTag(uuid)));
      tag.setTag("aliveMonsterIds", idList);
      tag.setBoolean("idle", idle);
    }

    compound.setTag("configTag", tag);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ConfigTileEntityAltarOfSpawning readFromNBTTagCompound(NBTTagCompound compound) {
    ConfigTileEntityAltarOfSpawning config = new ConfigTileEntityAltarOfSpawning();

    if (compound.hasKey("configTag")) {
      NBTTagCompound tag = compound.getCompoundTag("configTag");

      config.pos = NBTUtil.getPosFromTag(tag.getCompoundTag("pos"));

      if (tag.hasKey("spawnInfo")) {
        SpawnInfo spawnInfo = new SpawnInfo();
        config.currentSpawnInfo = spawnInfo.loadFromNBTData(tag.getCompoundTag("spawnInfo"));

        if (Strings.isNullOrEmpty(config.currentSpawnInfo.key)) {
          config.currentSpawnInfo = null;
        }

        config.bossSpawned = tag.getBoolean("bossSpawned");
        config.totalLightningBolts = tag.getInteger("totalLightningBolts");
        config.ticksUntilNextLightningBolt = tag.getInteger("ticksUntilNextLightningBolt");

        NBTTagList preBossMinionTagList = tag.getTagList("preBossMinions", 10);

        if (!preBossMinionTagList.isEmpty()) {
          for (int i = 0; i < preBossMinionTagList.tagCount(); i++) {
            NBTTagCompound bossAddInfoCompound = preBossMinionTagList.getCompoundTagAt(i);
            BossAddInfo bossAddInfo = new BossAddInfo();
            config.preBossMinions.add(bossAddInfo.loadFromNBTData(bossAddInfoCompound));
          }
        }
      }

      NBTTagList aliveIdList = tag.getTagList("aliveMonsterIds", 10);
      for (int i = 0; i < aliveIdList.tagCount(); i++)
      {
        UUID id = NBTUtil.getUUIDFromTag(aliveIdList.getCompoundTagAt(i));
        config.aliveMonsterIds.add(id);
      }

      config.idle = tag.getBoolean("idle");
    }

    return config;
  }

  public void Initialize() {
    this.pos = new BlockPos(0, 0, 0);
    this.preBossMinions = new ArrayList<>();
    this.currentSpawnInfo = null;
    this.aliveMonsterIds = new ArrayList<>();
  }
}