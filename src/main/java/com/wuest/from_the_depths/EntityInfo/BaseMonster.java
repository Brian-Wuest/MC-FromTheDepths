package com.wuest.from_the_depths.EntityInfo;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseMonster {

  public static Random random;
  public String domain;
  public String name;
  public String displayName;
  public int maxHealth;
  public float attackDamage;
  public boolean alwaysShowDisplayName;
  public int timeToWaitBeforeSpawn;
  public ArrayList<DropInfo> additionalDrops;

  static {
    BaseMonster.random = new Random();
  }

  public BaseMonster() {
    this.maxHealth = -1;
    this.attackDamage = -1;
    this.alwaysShowDisplayName = false;
    this.additionalDrops = new ArrayList<DropInfo>();
    this.timeToWaitBeforeSpawn = 20;
  }

  public ResourceLocation createResourceLocation() {
    return new ResourceLocation(this.domain, this.name);
  }

  public Entity createEntityForWorld(World world, BlockPos pos) {
    ResourceLocation resourceLocation = this.createResourceLocation();
    Entity entity = EntityList.createEntityByIDFromName(resourceLocation, world);

    if (entity != null && entity instanceof EntityLiving) {
      EntityLiving entityLiving = (EntityLiving) entity;

      // Randomize the X and Z coordinates but within 3 blocks of the original block.
      // Also randomize the positive/negative.
      BlockPos spawnPos = this.determineSpawnPos(pos, world, entityLiving.height);

      if (spawnPos != null) {
        // Set attributes.
        if (this.maxHealth > 0) {
          entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.maxHealth);
          entityLiving.setHealth(this.maxHealth);
        }

        if (this.attackDamage > 0) {
          entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.attackDamage);
        }

        if (this.displayName != null && !this.displayName.isEmpty() && !this.displayName.trim().isEmpty()) {
          entityLiving.setCustomNameTag(this.displayName);

          entityLiving.setAlwaysRenderNameTag(this.alwaysShowDisplayName);
        }

        // Add a tracking tag to the entity's saved NBT data.
        NBTTagCompound trackingTag = new NBTTagCompound();

        NBTTagList additionalDropList = new NBTTagList();

        if (!this.additionalDrops.isEmpty()) {
          for (DropInfo dropInfo : this.additionalDrops) {
            NBTTagCompound dropInfoTagCompound = new NBTTagCompound();
            dropInfo.writeToNBT(dropInfoTagCompound);

            additionalDropList.appendTag(dropInfoTagCompound);
          }
        }

        trackingTag.setTag("additionalDrops", additionalDropList);

        // Write the custom tag to this entity.
        NBTTagCompound entityCompoundTag = entityLiving.getEntityData();
        entityCompoundTag.setTag("from_the_depths", trackingTag);

        entityLiving.serializeNBT();

        entityLiving.forceSpawn = true;
        entityLiving.rotationYawHead = entityLiving.rotationYaw;
        entityLiving.renderYawOffset = entityLiving.rotationYaw;
        entityLiving.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityLiving)),
            (IEntityLivingData) null);
        world.spawnEntity(entityLiving);

        world.addWeatherEffect(new EntityLightningBolt(world, (double) spawnPos.getX(), (double) spawnPos.getY(),
            (double) spawnPos.getZ(), true));

        entityLiving.playLivingSound();
      }
    }

    return entity;
  }

  public BlockPos determineSpawnPos(BlockPos originalPos, World world, float height) {
    BlockPos spawnPos = null;

    for (int i = 0; i < 10; i++) {
      int randomValue = BaseMonster.random.nextInt(100) % 2 == 0 ? -1 : 1;
      int randomX = (BaseMonster.random.nextInt(4) * randomValue) + (randomValue * 2);

      randomValue = BaseMonster.random.nextInt(100) % 2 == 0 ? -1 : 1;
      int randomZ = (BaseMonster.random.nextInt(4) * randomValue) + (randomValue * 2);
      spawnPos = new BlockPos(originalPos.getX() + randomX, originalPos.up(1).getY(), originalPos.getZ() + randomZ);

      if (world.isAirBlock(spawnPos)) {
        boolean enoughSpace = true;

        for (int j = 0; j < height; j++) {
          if (!world.isAirBlock(spawnPos.up(j))) {
            enoughSpace = false;
          }
        }

        // There is not enough space for the monster in this spot, try to generate
        // another random destination.
        if (!enoughSpace) {
          continue;
        }

        // There is enough height space for this monster, go down until ground level.
        while (world.isAirBlock(spawnPos)) {
          BlockPos tempPos = spawnPos.down();

          if (!world.isAirBlock(tempPos)) {
            break;
          }

          spawnPos = spawnPos.down();
        }
      } else {
        int count = 0;

        // There is solid ground here, go up until air is found.
        while (!world.isAirBlock(spawnPos)) {
          spawnPos = spawnPos.up();
          count++;

          // 20 Solid or non-air blocks in a row. Just break at this point. The monster
          // will be spawned in solid ground or in the void in a Sky-block world.
          if (count >= 20) {
            break;
          }
        }
      }

      // Always push it 1 block above ground to make sure that the entire entity is
      // out of the ground.
      spawnPos = spawnPos.up();
      return spawnPos;
    }

    return null;
  }

  public boolean isValidEntity(World world) {
    ResourceLocation resourceLocation = this.createResourceLocation();
    Entity entity = EntityList.createEntityByIDFromName(resourceLocation, world);

    if (entity != null && entity instanceof EntityLiving) {
      return true;
    }

    return false;
  }

  public NBTTagCompound createTag() {
    NBTTagCompound tag = new NBTTagCompound();

    this.writeToNBT(tag);

    return tag;
  }

  public void writeToNBT(NBTTagCompound tag) {
    tag.setString("domain", this.domain);
    tag.setString("name", this.name);

    if (this.displayName != null && !this.displayName.trim().isEmpty()) {
      tag.setString("displayName", this.displayName);
    }

    tag.setInteger("maxHealth", this.maxHealth);
    tag.setFloat("attackDamage", this.attackDamage);
    tag.setBoolean("alwaysShowDisplayName", this.alwaysShowDisplayName);
    tag.setInteger("timeToWaitBeforeSpawn", this.timeToWaitBeforeSpawn);

    NBTTagList additionalDrops = new NBTTagList();

    if (this.additionalDrops != null) {
      for (DropInfo info : this.additionalDrops) {
        NBTTagCompound dropInfo = new NBTTagCompound();
        info.writeToNBT(dropInfo);

        additionalDrops.appendTag(dropInfo);
      }

      tag.setTag("additionalDrops", additionalDrops);
    }
  }

  public void loadFromNBT(NBTTagCompound tag) {
    this.domain = tag.getString("domain");
    this.name = tag.getString("name");
    this.displayName = tag.getString("displayName");
    this.maxHealth = tag.getInteger("maxHealth");
    this.attackDamage = tag.getFloat("attackDamage");
    this.alwaysShowDisplayName = tag.getBoolean("alwaysShowDisplayName");
    this.timeToWaitBeforeSpawn = tag.getInteger("timeToWaitBeforeSpawn");

    if (tag.hasKey("additionalDrops")) {
      this.additionalDrops = new ArrayList<DropInfo>();

      NBTTagList dropList = tag.getTagList("additionalDrops", 10);

      if (!dropList.hasNoTags()) {
        for (int i = 0; i < dropList.tagCount(); i++) {
          NBTTagCompound dropInfoTag = dropList.getCompoundTagAt(i);
          DropInfo dropInfo = new DropInfo();
          dropInfo.loadFromNBTData(dropInfoTag);

          if (dropInfo.item != null && !dropInfo.item.isEmpty()) {
            this.additionalDrops.add(dropInfo);
          }
        }
      }
    }
  }

}