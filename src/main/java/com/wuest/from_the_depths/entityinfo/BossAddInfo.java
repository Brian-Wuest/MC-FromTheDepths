package com.wuest.from_the_depths.entityinfo;

import com.google.common.base.Predicates;
import com.wuest.from_the_depths.FromTheDepths;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class BossAddInfo extends BaseMonster implements INBTSerializable<BossAddInfo> {
	public int minSpawns;
	public int maxSpawns;
	public int timeBetweenSpawns;
	public boolean spawnBeforeBoss;
	public BossAddInfo nextWaveOfAdds;

	// spawn tracking fields.
	public boolean spawning;
	public int numberLeftToSpawn;
	public int timeUntilNextSpawn;

	public BossAddInfo() {
		super();

		this.minSpawns = 0;
		this.maxSpawns = 0;
		this.timeBetweenSpawns = 100;
		this.spawnBeforeBoss = false;
		this.timeBetweenSpawns = 20;
		this.nextWaveOfAdds = null;

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
		return this.nextWaveOfAdds != null;
	}

	/**
	 * This method determines how many adds to spawn. If there are any additional
	 * waves to spawn; this will also determine their count.
	 */
	public void determineNumberToSpawn(World world) {
		this.timeUntilNextSpawn = this.timeToWaitBeforeSpawn;

		if (this.minSpawns != this.maxSpawns) {
			this.numberLeftToSpawn = BossAddInfo.determineRandomInt(this.maxSpawns, world);
			if (this.numberLeftToSpawn < this.minSpawns) {
				this.numberLeftToSpawn = this.minSpawns;
			}
		} else {
			this.numberLeftToSpawn = this.maxSpawns;
		}

		if (this.nextWaveOfAdds != null) {
			this.nextWaveOfAdds.determineNumberToSpawn(world);
		}
	}

	/**
	 * Processes the spawning of this minion.
	 *
	 * @return True when this minion is done spawning; otherwise false.
	 */
	public boolean processMinionSpawning(World world, BlockPos pos, ICommandSender commandSender, List<UUID> entities) {
		this.spawning = true;

		if (this.numberLeftToSpawn == 0 && this.nextWaveOfAdds != null) {
			// This minion is done spawning but there is another wave, process that now.
			return this.nextWaveOfAdds.processMinionSpawning(world, pos, commandSender, entities);
		} else if (this.numberLeftToSpawn == 0) {
			// Spawning is complete and there are no more waves to process.
			return true;
		} else if (this.timeUntilNextSpawn <= 0) {
			// There is something to spawn and we reached the timer; do that now.
			Entity entity = this.createEntityForWorld(world, pos.up(), null, commandSender);
			entities.add(entity.getUniqueID());
			//System.out.println("added " + entity.getDisplayName().getFormattedText() + " to the list");

			if (entity == null) {
				TextComponentString component = new TextComponentString(
						"Unable to summon additional monster due to limited air space in summoning area");

				for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class,
						Predicates.and(EntitySelectors.IS_ALIVE, EntitySelectors.withinRange(pos.getX(), pos.getY(), pos.getZ(), 15)))) {
					player.sendMessage(component);
				}
			}

			// Decrement the number left to spawn and update the timer for the next spawn.
			this.numberLeftToSpawn--;
			this.timeUntilNextSpawn = this.timeBetweenSpawns;

			if (this.numberLeftToSpawn == 0 && this.nextWaveOfAdds == null) {
				// This minion is done spawning and there are no more waves, tell the calling
				// method that this one is done.
				return true;
			}
		} else {
			// Not time yet to spawn; just decrement the timer.
			this.timeUntilNextSpawn--;
		}

		return false;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("minSpawns", this.minSpawns);
		tag.setInteger("maxSpawns", this.maxSpawns);
		tag.setInteger("timeBetweenSpawns", this.timeBetweenSpawns);
		tag.setBoolean("spawnBeforeBoss", this.spawnBeforeBoss);
		tag.setBoolean("spawning", this.spawning);
		tag.setInteger("numberLeftToSpawn", this.numberLeftToSpawn);
		tag.setInteger("timeUntilNextSpawn", this.timeUntilNextSpawn);

		if (this.nextWaveOfAdds != null) {
			NBTTagCompound nextWaveTag = new NBTTagCompound();

			this.nextWaveOfAdds.writeToNBT(nextWaveTag);
			tag.setTag("nextWaveOfAdds", nextWaveTag);
		}
	}

	public BossAddInfo loadFromNBTData(NBTTagCompound tag) {
		super.loadFromNBT(tag);

		this.minSpawns = tag.getInteger("minSpawns");
		this.maxSpawns = tag.getInteger("maxSpawns");
		this.timeBetweenSpawns = tag.getInteger("timeBetweenSpawns");
		this.spawnBeforeBoss = tag.getBoolean("spawnBeforeBoss");
		this.spawning = tag.getBoolean("spawning");
		this.numberLeftToSpawn = tag.getInteger("numberLeftTospawn");
		this.timeUntilNextSpawn = tag.getInteger("timeUntilNextSpawn");

		if (this.minSpawns < 0) {
			FromTheDepths.logger.warn(
					String.format("The mininum number of spawns ({}) for monster {} is not valid, setting to default of zero",
							this.minSpawns, this.name));
		}

		if (this.maxSpawns < 0 || this.maxSpawns < this.minSpawns) {
			FromTheDepths.logger.warn(String.format(
					"The maximum number of spawns ({}) for monster {} is not valid, setting to minimum number of spawns ({}",
					this.maxSpawns, this.maxHealth, this.minSpawns));
		}

		if (this.timeBetweenSpawns < 0) {
			FromTheDepths.logger.warn(
					String.format("The time between spawns ({}) for monster {} is not valid, setting to default of 100 ticks",
							this.timeBetweenSpawns, this.name));
			this.timeBetweenSpawns = 100;
		}

		if (tag.hasKey("nextWaveOfAdds")) {
			NBTTagCompound nextWaveTag = tag.getCompoundTag("nextWaveOfAdds");
			BossAddInfo nextWaveAddInfo = new BossAddInfo();
			this.nextWaveOfAdds = nextWaveAddInfo.loadFromNBTData(nextWaveTag);
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
		newInstance.spawnBeforeBoss = this.spawnBeforeBoss;
		newInstance.timeToWaitBeforeSpawn = this.timeToWaitBeforeSpawn;
		newInstance.spawning = this.spawning;
		newInstance.numberLeftToSpawn = this.numberLeftToSpawn;
		newInstance.timeUntilNextSpawn = this.timeUntilNextSpawn;
		newInstance.commandToRunAtSpawn = this.commandToRunAtSpawn;
		newInstance.nbt = this.nbt;
		newInstance.spawnEffect = this.spawnEffect;
		newInstance.shouldSpawnInAir = this.shouldSpawnInAir;
		
		if (this.nextWaveOfAdds != null) {
			newInstance.nextWaveOfAdds = this.nextWaveOfAdds.clone();
		}

		for (DropInfo info : this.additionalDrops) {
			newInstance.additionalDrops.add(info.clone());
		}

		return newInstance;
	}
}