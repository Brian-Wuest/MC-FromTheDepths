package com.wuest.from_the_depths.EntityInfo;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BossInfo
{
	public static Random random;
	public String domain;
	public String name;
	public NBTTagCompound nbtData;
	
	static 
	{
		BossInfo.random = new Random();
	}
	
	public BossInfo()
	{
		this.nbtData = new NBTTagCompound();
	}
	
	public BossInfo(BossInfo oldInstance)
	{
		this();
		this.domain = oldInstance.domain;
		this.name = oldInstance.name;
		this.nbtData = oldInstance.nbtData;
	}
	
	public ResourceLocation createResourceLocation()
	{
		return new ResourceLocation(this.domain, this.name);
	}
	
	public Entity createEntityForWorld(World world, BlockPos pos)
	{
		ResourceLocation resourceLocation = this.createResourceLocation();
		Entity entity = EntityList.createEntityByIDFromName(resourceLocation, world);
		
		if (entity != null && entity instanceof EntityLiving)
		{
			EntityLiving entityLiving = (EntityLiving)entity;
			
			// Randomize the X and Z coordinates but within 3 blocks of the original block.
			// Also randomize the positive/negative.
			int randomX = BossInfo.random.nextInt(3) * (BossInfo.random.nextInt(100) % 2 == 0 ? -1 : 1);
            int randomZ = BossInfo.random.nextInt(3) * (BossInfo.random.nextInt(100) % 2 == 0 ? -1 : 1);
            BlockPos spawnPos = new BlockPos(pos.getX() + randomX, pos.up(1).getY(), pos.getZ() + randomZ);
			
            if (spawnPos.getX() == pos.getX() && spawnPos.getZ() == pos.getZ())
            {
            	// Never let the spawn to be on-top of the block, make it right next to the block.
            	spawnPos.north().east();
            }
            
			// When NBT data is provided, everything needs to be provided. Position information is updated to be appropriate for this world.
			if (this.nbtData != null && !this.nbtData.hasNoTags())
			{
				entityLiving.deserializeNBT(this.nbtData);
			}
			else
			{
				entityLiving.serializeNBT();
			}
			
			entityLiving.forceSpawn = true;
            entityLiving.rotationYawHead = entityLiving.rotationYaw;
            entityLiving.renderYawOffset = entityLiving.rotationYaw;
			entityLiving.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
			world.spawnEntity(entityLiving);
			
			world.addWeatherEffect(new EntityLightningBolt(world, (double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), true));
			entityLiving.playLivingSound();
		}
		
		return entity;
	}
	
	public boolean isValidEntity(World world)
	{
		ResourceLocation resourceLocation = this.createResourceLocation();
		Entity entity = EntityList.createEntityByIDFromName(resourceLocation, world);
		
		if (entity != null && entity instanceof EntityLiving)
		{
			return true;
		}
		
		return false;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("domain", this.domain);
		tag.setString("name", this.name);
		tag.setTag("nbtData", this.nbtData);
	}
	
	public static BossInfo CreateFromNBT(NBTTagCompound tag)
	{
		BossInfo returnValue = new BossInfo();
		
		if (tag.hasKey("domain"))
		{
			returnValue.domain = tag.getString("domain");
		}
		
		if (tag.hasKey("name"))
		{
			returnValue.name = tag.getString("name");
		}
		
		if (tag.hasKey("nbtData"))
		{
			returnValue.nbtData = tag.getCompoundTag("nbtData");
		}
		
		return returnValue;
	}
}