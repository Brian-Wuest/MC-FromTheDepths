package com.wuest.from_the_depths.TileEntities;

import com.wuest.from_the_depths.Base.TileEntityBase;
import com.wuest.from_the_depths.Config.ConfigTileEntityAltarOfSpawning;

import net.minecraft.tileentity.TileEntity;

public class TileEntityAltarOfSpawning extends TileEntityBase<ConfigTileEntityAltarOfSpawning>
{
	/**
     * Like the old updateEntity(), except more generic.
     */
	@Override
	public void update()
	{
		if (this.config.currentSpawnInfo != null
				&& this.config.currentSpawnInfo.bossAddInfo != null)
		{
			this.config.ticksForCurrentSpawn++;
			
			if (this.config.ticksForCurrentSpawn >= this.config.currentSpawnInfo.bossAddInfo.spawnFrequency
					&& this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration > 0)
			{
				this.markDirty();
				
				// We are past time to spawn an add so, decrement the time remaining and spawn an add.
				this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration = this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration - this.config.ticksForCurrentSpawn;
				this.config.ticksForCurrentSpawn = 0;
				
				// Spawn the entity in this world above this block.
				this.config.currentSpawnInfo.bossAddInfo.createEntityForWorld(this.world, this.pos.up());
				
			}
			else if (this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration <= 0)
			{
				this.markDirty();
				
				// No longer need to keep the spawn information in this tile entity since no more adds can be spawned.
				// Just remove it and mark this tile entity as dirty.
				this.config.currentSpawnInfo = null;
			}
		}
	}
}