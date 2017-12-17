package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BossInfo
{
	public String domain;
	public String name;
	public NBTTagCompound nbtData;
	
	public BossInfo()
	{
	}
	
	public ResourceLocation createResourceLocation()
	{
		return new ResourceLocation(this.domain, this.name);
	}
	
	public Entity createEntityForWorld(World world, BlockPos pos)
	{
		ResourceLocation resourceLocation = this.createResourceLocation();
		Entity entity = EntityList.createEntityByIDFromName(resourceLocation, world);
		
		if (entity != null)
		{
			if (entity instanceof EntityLiving)
			{
				EntityLiving entityLiving = (EntityLiving)entity;
				
				// When NBT data is provided, everything needs to be provided. Position information is updated to be appropriate for this world.
				if (this.nbtData != null && !this.nbtData.hasNoTags())
				{
					entityLiving.deserializeNBT(this.nbtData);
				}
				
				entityLiving.forceSpawn = true;
                entityLiving.rotationYawHead = entityLiving.rotationYaw;
                entityLiving.renderYawOffset = entityLiving.rotationYaw;
				entityLiving.setPositionAndUpdate(pos.getX(), pos.up(1).getY(), pos.getZ());
				entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
				world.spawnEntity(entityLiving);
				entityLiving.playLivingSound();
			}
		}
		
		return entity;
	}
}