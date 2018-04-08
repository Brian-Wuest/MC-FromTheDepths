package com.wuest.from_the_depths.TileEntities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Base.TileEntityBase;
import com.wuest.from_the_depths.Config.ConfigTileEntityAltarOfSpawning;
import com.wuest.from_the_depths.EntityInfo.SpawnInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityAltarOfSpawning extends TileEntityBase<ConfigTileEntityAltarOfSpawning>
{
    private static final Predicate<EntityPlayerMP> VALID_PLAYER = Predicates.<EntityPlayerMP>and(EntitySelectors.IS_ALIVE, EntitySelectors.withinRange(0.0D, 15.0D, 0.0D, 15.0D));
    
	public TileEntityAltarOfSpawning()
	{
		super();
		this.config = new ConfigTileEntityAltarOfSpawning();
	}
	
	@Override
    public void setPos(BlockPos posIn)
    {
        this.pos = posIn.toImmutable();
        this.getConfig().pos = this.pos;
    }
	
    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
	public void markDirty()
    {
    	super.markDirty();
    	
    	if (!this.world.isRemote)
    	{
    		MinecraftServer server = this.world.getMinecraftServer();
    		server.getPlayerList().sendPacketToAllPlayers(this.getUpdatePacket());
    	}
    }
	
    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
    	if (this.getConfig().currentSpawnInfo != null
    			&& this.config.currentSpawnInfo.bossAddInfo != null)
    	{
    		String display = "";
    		
    		if (this.getConfig().spawningBoss)
    		{
    			display = TextFormatting.UNDERLINE.toString() + "Spawning: " + TextFormatting.RESET.toString() + "Boss";
    		}
    		else
    		{
	    		ResourceLocation resourceLocation = new ResourceLocation(this.config.currentSpawnInfo.bossAddInfo.domain, this.config.currentSpawnInfo.bossAddInfo.name);
	    		Entity entity = EntityList.createEntityByIDFromName(resourceLocation, this.world);
	    		
	    		display = TextFormatting.UNDERLINE.toString() + "Spawning: " + TextFormatting.RESET.toString() + entity.getName();
    		}
    		
    		return new TextComponentString(display);
    	}
    	
        return null;
    }
	
	/**
     * Like the old updateEntity(), except more generic.
     */
	@Override
	public void update()
	{
		// When this is a peaceful world, don't allow this process to continue.
		if (!this.world.isRemote && this.world.getDifficulty() != EnumDifficulty.PEACEFUL)
		{
			if (this.config.currentSpawnInfo != null)
			{
				if (this.config.spawningBoss)
				{
					if (this.config.totalLightningBolts >= 4)
					{
						Entity entity = this.config.currentSpawnInfo.bossInfo.createEntityForWorld(this.world, this.pos);
						
						if (entity == null)
						{
							TextComponentString component = new TextComponentString("Unable to summon boss monster due to limited air space in summoning area");

					        for (EntityPlayerMP player : this.world.getPlayers(EntityPlayerMP.class, VALID_PLAYER))
					        {
					        	player.sendMessage(component);
					        }
						}
						
						this.config.spawningBoss = false;
						
						if (this.config.currentSpawnInfo.bossAddInfo == null)
						{
							this.config.currentSpawnInfo = null;
						}
						
						this.markDirty();
					}
					else if (this.config.ticksUntilNextLightningBolt - 1 <= 0)
					{
						EnumFacing facing = EnumFacing.NORTH;
						
						switch (this.config.totalLightningBolts)
						{
							case 0:
							{
								facing = EnumFacing.NORTH;
								break;
							}
							
							case 1: 
							{
								facing = EnumFacing.EAST;
								break;
							}
							
							case 2: 
							{
								facing = EnumFacing.SOUTH;
								break;
							}
							
							case 3:
							{
								facing = EnumFacing.WEST;
								break;
							}
						}
						
						BlockPos lightningBoltPos = this.pos.offset(facing, 2);
						world.addWeatherEffect(new EntityLightningBolt(world, (double)lightningBoltPos.getX(), (double)lightningBoltPos.getY(), (double)lightningBoltPos.getZ(), true));
						
						this.config.totalLightningBolts++;
						this.config.ticksUntilNextLightningBolt = ModRegistry.AlterOfSpawning().tickRate(this.world);
						
						// Don't use this class's mark dirty method as it will send too many packets to players.
						super.markDirty();
					}
					else
					{
						this.config.ticksUntilNextLightningBolt--;
						
						// Don't use this class's mark dirty method as it will send too many packets to players.
						super.markDirty();
					}
				}
				else if (this.config.currentSpawnInfo.bossAddInfo != null)
				{
					this.config.ticksForCurrentSpawn++;
					
					if (this.config.ticksForCurrentSpawn >= this.config.currentSpawnInfo.bossAddInfo.spawnFrequency
							&& this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration > 0)
					{
						// We are past time to spawn an add so, decrement the time remaining and spawn an add.
						this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration = this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration - this.config.ticksForCurrentSpawn;
						this.config.ticksForCurrentSpawn = 0;
						
						// Spawn the entity in this world above this block.
						Entity entity = this.config.currentSpawnInfo.bossAddInfo.createEntityForWorld(this.world, this.pos.up());
						
						if (entity == null)
						{
							TextComponentString component = new TextComponentString("Unable to summon additional monster due to limited air space in summoning area");

					        for (EntityPlayerMP player : this.world.getPlayers(EntityPlayerMP.class, VALID_PLAYER))
					        {
					        	player.sendMessage(component);
					        }
						}
						
						this.markDirty();
						
					}
					else if (this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration <= 0)
					{
						// No longer need to keep the spawn information in this tile entity since no more adds can be spawned.
						// Just remove it and mark this tile entity as dirty.
						this.config.currentSpawnInfo = null;
						
						this.markDirty();
					}
				}
			}
		}
	}
	
	public void InitiateSpawning(SpawnInfo spawnInfo, int tickRate)
	{
		this.config.spawningBoss = true;
		this.config.totalLightningBolts = 0;
		this.config.ticksUntilNextLightningBolt = tickRate;
		this.config.currentSpawnInfo = spawnInfo;
		
		if (this.config.currentSpawnInfo.bossAddInfo != null)
		{
			this.config.currentSpawnInfo.bossAddInfo.spawnFrequency = this.config.currentSpawnInfo.bossAddInfo.spawnFrequency * tickRate;
			this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration = this.config.currentSpawnInfo.bossAddInfo.totalSpawnDuration * tickRate;
		}
		
		this.markDirty();
	}
}