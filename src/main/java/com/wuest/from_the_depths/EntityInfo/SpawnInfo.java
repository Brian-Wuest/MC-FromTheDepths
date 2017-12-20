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
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("key", this.key);
		NBTTagCompound bossInfoTag = new NBTTagCompound();
		NBTTagCompound bossAddInfoTag = new NBTTagCompound();
		
		this.bossInfo.writeToNBT(bossInfoTag);
		tag.setTag("bossInfo", bossInfoTag);
		
		if (this.bossAddInfo != null)
		{
			this.bossAddInfo.writeToNBT(bossAddInfoTag);
			tag.setTag("bossAddInfo", bossAddInfoTag);
		}
	}
	
	public static SpawnInfo loadFromNBTData(NBTTagCompound nbtData)
	{
		SpawnInfo spawnInfo = new SpawnInfo();
		
		if (nbtData.hasKey("key"))
		{
			spawnInfo.key = nbtData.getString("key");
		}
		
		if (nbtData.hasKey("bossInfo"))
		{
			NBTTagCompound bossCompound = nbtData.getCompoundTag("bossInfo");
			spawnInfo.bossInfo = BossInfo.CreateFromNBT(bossCompound);
		}
		
		if (nbtData.hasKey("bossAddInfo"))
		{
			NBTTagCompound bossAddInfo = nbtData.getCompoundTag("bossAddInfo");
			spawnInfo.bossAddInfo = BossAddInfo.CreateFromNBT(bossAddInfo);
		}
		
		return spawnInfo;	
	}
}