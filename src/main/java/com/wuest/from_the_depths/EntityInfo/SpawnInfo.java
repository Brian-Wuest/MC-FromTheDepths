package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.nbt.NBTTagCompound;

public class SpawnInfo
{
	public String key;
	public BossInfo bossInfo;
	public BossAddInfo bossAddInfo;
		
	public SpawnInfo()
	{
	}
	
	public SpawnInfo(BossInfo bossInfo)
	{
		this();
		this.bossInfo = bossInfo;
	}
	
	public SpawnInfo (BossInfo bossInfo, BossAddInfo bossAddInfo)
	{
		this(bossInfo);
		this.bossAddInfo = bossAddInfo;
	}
	
/*	public static SpawnInfo loadFromNBTData(NBTTagCompound nbtData) throws Exception
	{
		SpawnInfo spawnInfo = new SpawnInfo();
		
		if (nbtData.hasKey("key"))
		{
			spawnInfo.key = nbtData.getString("key");
		}
		else
		{
			throw new Exception("NBT Data does not contain a Key, this is required.");
		}
		
		if (nbtData.hasKey("bossInfo"))
		{
			NBTTagCompound bossCompound = nbtData.getCompoundTag("bossInfo");
			spawnInfo.bossInfo = BossInfo.CreateFromNBT(bossCompound);
		}
		else
		{
			throw new Exception("NBT Data does not contain boss information, this is required.");
		}
		
		if (nbtData.hasKey("bossAddInfo"))
		{
			NBTTagCompound bossAddInfo = nbtData.getCompoundTag("bossAddInfo");
			spawnInfo.bossAddInfo = BossAddInfo.CreateFromNBT(bossAddInfo);
		}
		
		return spawnInfo;	
	}*/
}