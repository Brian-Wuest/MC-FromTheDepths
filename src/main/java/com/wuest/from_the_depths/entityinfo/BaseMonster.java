package com.wuest.from_the_depths.entityinfo;

import com.google.common.base.Predicate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wuest.from_the_depths.FromTheDepths;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class BaseMonster {
	public String domain;
	public String name;
	public String displayName;
	public int maxHealth;
	public float attackDamage;
	public boolean alwaysShowDisplayName;
	public int timeToWaitBeforeSpawn;
	public ArrayList<DropInfo> additionalDrops;
	public String commandToRunAtSpawn;
	public JsonObject nbt;
	public SpawnEffectEnum spawnEffect;
	public boolean shouldSpawnInAir;
	public String warningMessage;
	public String spawnedMessage;
	public int idleTimeBeforeDespawning;

	public BaseMonster() {
		this.maxHealth = -1;
		this.attackDamage = -1;
		this.alwaysShowDisplayName = false;
		this.additionalDrops = new ArrayList<>();
		this.timeToWaitBeforeSpawn = 20;
		this.commandToRunAtSpawn = "";
		this.nbt = null;
		this.spawnEffect = SpawnEffectEnum.NONE;
		this.shouldSpawnInAir = false;
		this.warningMessage = "";
		this.spawnedMessage = "";
		this.idleTimeBeforeDespawning = -1;
	}

	public ResourceLocation createResourceLocation() {
		return new ResourceLocation(this.domain, this.name);
	}

	public Entity createEntityForWorld(World world, BlockPos pos, @Nullable Predicate<EntityPlayerMP> playerValid, ICommandSender commandSender) {
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
					IAttributeInstance entityAttribute = entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

					if (entityAttribute != null) {
						entityAttribute.setBaseValue(this.attackDamage);
					}
				}

				if (this.displayName != null && !this.displayName.isEmpty() && !this.displayName.trim().isEmpty()) {
					entityLiving.setCustomNameTag(this.displayName);

					entityLiving.setAlwaysRenderNameTag(this.alwaysShowDisplayName);
				}

				// Add a tracking tag to the entity's saved NBT data.
				NBTTagCompound fromTheDepthsTag = new NBTTagCompound();

				NBTTagList additionalDropList = new NBTTagList();

				if (!this.additionalDrops.isEmpty()) {
					for (DropInfo dropInfo : this.additionalDrops) {
						NBTTagCompound dropInfoTagCompound = new NBTTagCompound();
						dropInfo.writeToNBT(dropInfoTagCompound);

						additionalDropList.appendTag(dropInfoTagCompound);
					}
				}

				fromTheDepthsTag.setTag("additionalDrops", additionalDropList);

				if (!fromTheDepthsTag.hasKey("timeUntilDespawn") && this.idleTimeBeforeDespawning > 0) {
					fromTheDepthsTag.setInteger("timeUntilDespawn", this.idleTimeBeforeDespawning);
					fromTheDepthsTag.setLong("tilePos", pos.toLong());
				}

				// Write the custom tag to this entity.
				NBTTagCompound entityCompoundTag = entityLiving.getEntityData();
				entityCompoundTag.setTag("from_the_depths", fromTheDepthsTag);

				entityLiving.forceSpawn = true;
				entityLiving.rotationYawHead = entityLiving.rotationYaw;
				entityLiving.renderYawOffset = entityLiving.rotationYaw;
				entityLiving.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
				entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(spawnPos)),
						null);

				// Serialize the entity NBT data so it can be updated.
				NBTTagCompound serializedEntity = entityLiving.serializeNBT();

				// This has to be after the "onInitialSpawn" call since some monsters will set properties to random values which need to be set specifically.
				if (this.nbt != null) {
					NBTTagCompound compound = null;
					try {
						compound = JsonToNBT.getTagFromJson(this.nbt.toString());
					} catch (NBTException exception) {
						FromTheDepths.logger.error(exception);
					}

					if (compound != null && !compound.isEmpty()) {
						for (String tagKey : compound.getKeySet()) {
							serializedEntity.setTag(tagKey, compound.getTag(tagKey));
						}

						entityLiving = (EntityLiving) EntityList.createEntityFromNBT(serializedEntity, world);
					}
				}

				world.spawnEntity(entityLiving);

				if (this.spawnEffect == SpawnEffectEnum.LIGHTNING) {
					world.addWeatherEffect(new EntityLightningBolt(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), true));
				}

				//Boss Spawned Message | if the predicate is not null it means we're spawning the actual boss and not minions
				if (playerValid != null) {
					world.getPlayers(EntityPlayerMP.class, playerValid).forEach(player ->
							player.sendMessage(new TextComponentString(this.spawnedMessage))
					);
				}

				entityLiving.playLivingSound();

				if (this.commandToRunAtSpawn != null && !this.commandToRunAtSpawn.isEmpty()) {
					world.getMinecraftServer().getCommandManager().executeCommand(commandSender, this.commandToRunAtSpawn);
				}
			}
		}

		return entity;
	}

	public BlockPos determineSpawnPos(BlockPos originalPos, World world, float height) {
		BlockPos spawnPos = null;

		// The +1 is there since we don't count the altar's position.
		int radius = FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius + 1;
		int spawningHeight = FromTheDepths.proxy.getServerConfiguration().altarSpawningHeight;

		for (int i = 0; i < 10; i++) {
			int randomX = this.determineSpawnAxisValue(radius, world);
			int randomZ = this.determineSpawnAxisValue(radius, world);

			// Determine if the monster should spawn in the air.
			// If so make the spawning height equal to the configured area size.
			int spawnHeight = this.shouldSpawnInAir ? spawningHeight : 1;

			spawnPos = new BlockPos(originalPos.getX() + randomX, originalPos.up(spawnHeight).getY(), originalPos.getZ() + randomZ);

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

	public int determineSpawnAxisValue(int radius, World world) {
		int randomValue = BaseMonster.determineRandomInt(100, world) % 2 == 0 ? -1 : 1;
		int randomAxis = (BaseMonster.determineRandomInt(radius, world) * randomValue) + (randomValue * 2);

		// The radius Blocks is the max for a block position.
		return Math.abs(randomAxis) > radius ? radius * randomValue : randomAxis;
	}

	public static int determineRandomInt(int bound, World world) {
		int randomCounter = world.rand.nextInt(21);
		int returnValue = 1;

		for (int i = 0; i < randomCounter; i++) {
			returnValue = world.rand.nextInt(bound);
		}

		return returnValue;
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
		tag.setString("commandToRunAtSpawn", this.commandToRunAtSpawn);
		tag.setString("spawnEffect", this.spawnEffect.getName());
		tag.setBoolean("shouldSpawnInAir", this.shouldSpawnInAir);
		tag.setString("warningMessage", this.warningMessage);
		tag.setString("spawnedMessage", this.spawnedMessage);
		tag.setInteger("idlingSecondsBeforeDespawn", this.idleTimeBeforeDespawning);

		if (this.nbt != null) {
			tag.setString("nbt", this.nbt.toString());
		}

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
		this.commandToRunAtSpawn = tag.getString("commandToRunAtSpawn");
		this.spawnEffect = SpawnEffectEnum.getFromName(tag.getString("spawnEffect"));
		this.shouldSpawnInAir = tag.getBoolean("shouldSpawnInAir");
		this.warningMessage = tag.getString("warningMessage");
		this.spawnedMessage = tag.getString("spawnedMessage");
		this.idleTimeBeforeDespawning = tag.getInteger("idlingSecondsBeforeDespawn");

		if (tag.hasKey("nbt")) {
			JsonParser parser = new JsonParser();
			this.nbt = (JsonObject) parser.parse(tag.getString("nbt"));
		}

		if (tag.hasKey("additionalDrops")) {
			this.additionalDrops = new ArrayList<DropInfo>();

			NBTTagList dropList = tag.getTagList("additionalDrops", 10);

			if (!dropList.isEmpty()) {
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

	@Override
	public String toString()
	{
		return "BaseMonster{" +
				"domain='" + domain + '\'' +
				", name='" + name + '\'' +
				", displayName='" + displayName + '\'' +
				", maxHealth=" + maxHealth +
				", attackDamage=" + attackDamage +
				", alwaysShowDisplayName=" + alwaysShowDisplayName +
				", timeToWaitBeforeSpawn=" + timeToWaitBeforeSpawn +
				'}';
	}
}