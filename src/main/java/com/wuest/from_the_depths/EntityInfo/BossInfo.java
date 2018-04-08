package com.wuest.from_the_depths.EntityInfo;

import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BossInfo
{
	public static Random random;
	public String domain;
	public String name;
	public String displayName;
	public int maxHealth;
	public float attackDamage;
	public boolean alwaysShowDisplayName;
	
	static 
	{
		BossInfo.random = new Random();
	}
	
	public BossInfo()
	{
		this.maxHealth = -1;
		this.attackDamage = -1;
		this.alwaysShowDisplayName = false;
	}
	
	public BossInfo(BossInfo oldInstance)
	{
		this();
		this.domain = oldInstance.domain;
		this.name = oldInstance.name;
		this.displayName = oldInstance.displayName;
		this.maxHealth = oldInstance.maxHealth;
		this.attackDamage = oldInstance.attackDamage;
		this.alwaysShowDisplayName = oldInstance.alwaysShowDisplayName;
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
			BlockPos spawnPos = this.determineSpawnPos(pos, world, entityLiving.height);
			
			if (spawnPos != null)
			{
				// Set attributes.
				if (this.maxHealth > 0)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.maxHealth);
					entityLiving.setHealth(this.maxHealth);
				}
				
				if (this.attackDamage > 0)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.attackDamage);
				}
				
				if (this.displayName != null && !this.displayName.isEmpty() && !this.displayName.trim().isEmpty())
				{
					entityLiving.setCustomNameTag(this.displayName);
					
					entityLiving.setAlwaysRenderNameTag(this.alwaysShowDisplayName);
				}
				
				entityLiving.serializeNBT();
				
				entityLiving.forceSpawn = true;
	            entityLiving.rotationYawHead = entityLiving.rotationYaw;
	            entityLiving.renderYawOffset = entityLiving.rotationYaw;
				entityLiving.setPositionAndUpdate(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
				entityLiving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityLiving)), (IEntityLivingData)null);
				world.spawnEntity(entityLiving);
				
				world.addWeatherEffect(new EntityLightningBolt(world, (double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), true));
				entityLiving.playLivingSound();
			}
		}
		
		return entity;
	}
	
	public BlockPos determineSpawnPos(BlockPos originalPos, World world, float height)
	{
		BlockPos spawnPos = null;
		
		for (int i = 0; i < 10; i++)
		{
			int randomValue = BossInfo.random.nextInt(100) % 2 == 0 ? -1 : 1;
			int randomX = (BossInfo.random.nextInt(4) * randomValue) + (randomValue * 2);
			
			randomValue = BossInfo.random.nextInt(100) % 2 == 0 ? -1 : 1;
	        int randomZ = (BossInfo.random.nextInt(4) * randomValue) + (randomValue * 2);
	        spawnPos = new BlockPos(originalPos.getX() + randomX, originalPos.up(1).getY(), originalPos.getZ() + randomZ);
	        
	        if (world.isAirBlock(spawnPos))
	        {
	        	boolean enoughSpace = true;
	        	
	        	for (int j = 0; j < height; j++)
	        	{
	        		if (!world.isAirBlock(spawnPos.up(j)))
	        		{
	        			enoughSpace = false;
	        		}
	        	}
	        	
	        	// There is not enough space for the monster in this spot, try to generate another random destination.
	        	if (!enoughSpace)
	        	{
	        		continue;
	        	}
	        	
	        	// There is enough height space for this monster, go down until ground level.
	            while (world.isAirBlock(spawnPos))
	            {
	            	BlockPos tempPos = spawnPos.down();
	            	
	            	if (!world.isAirBlock(tempPos))
	            	{
	            		break;
	            	}
	            	
	            	spawnPos = spawnPos.down();
	            }
	        }
	        else
	        {
	        	// There is solid ground here, go up until air is found.
	        	while (!world.isAirBlock(spawnPos))
	        	{
	        		spawnPos = spawnPos.up();
	        	}
	        }
	        
	        // Always push it 1 block above ground to make sure that the entire entity is out of the ground.
	        spawnPos = spawnPos.up();
	        return spawnPos;
		}
		
        return null;
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
		
		if (this.displayName != null && !this.displayName.trim().isEmpty())
		{
			tag.setString("displayName", this.displayName);
		}
		
		tag.setInteger("maxHealth", this.maxHealth);
		tag.setFloat("attackDamage", this.attackDamage);
		tag.setBoolean("alwaysShowDisplayName", this.alwaysShowDisplayName);
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
		
		if (tag.hasKey("displayName"))
		{
			returnValue.displayName = tag.getString("displayName");
		}
		
		if (tag.hasKey("maxHealth"))
		{
			returnValue.maxHealth = tag.getInteger("maxHealth");
		}
		
		if (tag.hasKey("attackDamage"))
		{
			returnValue.attackDamage = tag.getFloat("attackDamage");
		}
		
		if (tag.hasKey("alwaysShowDisplayName"))
		{
			returnValue.alwaysShowDisplayName = tag.getBoolean("alwaysShowDisplayName");
		}
		
		return returnValue;
	}
}