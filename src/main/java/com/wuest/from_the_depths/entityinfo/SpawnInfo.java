package com.wuest.from_the_depths.entityinfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;

public class SpawnInfo implements INBTSerializable<SpawnInfo> {
    public String key;
    public BossInfo bossInfo;
    public ArrayList<BossAddInfo> bossAddInfo;

    public SpawnInfo() {
        this.bossAddInfo = new ArrayList<>();
    }

    public SpawnInfo(BossInfo bossInfo) {
        this();
        this.bossInfo = bossInfo;
    }

    public SpawnInfo(BossInfo bossInfo, ArrayList<BossAddInfo> bossAddInfo) {
        this(bossInfo);

        this.bossAddInfo = bossAddInfo == null ? new ArrayList<>() : bossAddInfo;
    }

    public SpawnInfo clone() {
        SpawnInfo newInstance = new SpawnInfo();

        newInstance.key = this.key;

        newInstance.bossInfo = new BossInfo();
        newInstance.bossAddInfo = new ArrayList<>();

        if (this.bossInfo != null) {
            newInstance.bossInfo = this.bossInfo.clone();
        }

        if (this.bossAddInfo != null) {

            for (BossAddInfo addInfo : this.bossAddInfo) {
                newInstance.bossAddInfo.add(addInfo.clone());
            }
        }

        return newInstance;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("key", this.key);
        NBTTagCompound bossInfoTag = new NBTTagCompound();
        NBTTagList bossAddInfoTag = new NBTTagList();

        this.bossInfo.writeToNBT(bossInfoTag);
        tag.setTag("bossInfo", bossInfoTag);

        if (this.bossAddInfo != null) {
            for (BossAddInfo addInfo : this.bossAddInfo) {
                NBTTagCompound addInfoTag = new NBTTagCompound();
                addInfo.writeToNBT(addInfoTag);

                bossAddInfoTag.appendTag(addInfoTag);
            }

            tag.setTag("bossAddInfoList", bossAddInfoTag);
        }
    }

    public SpawnInfo loadFromNBTData(NBTTagCompound nbtData) {
        SpawnInfo spawnInfo = new SpawnInfo();

        if (nbtData.hasKey("key")) {
            spawnInfo.key = nbtData.getString("key");
        }

        if (nbtData.hasKey("bossInfo")) {
            NBTTagCompound bossCompound = nbtData.getCompoundTag("bossInfo");
            BossInfo bossInfo = new BossInfo();
            spawnInfo.bossInfo = bossInfo.loadFromNBTData(bossCompound);
        }

        if (nbtData.hasKey("bossAddInfoList")) {
            NBTTagList bossAddInfo = nbtData.getTagList("bossAddInfoList", 10);

            spawnInfo.bossAddInfo = new ArrayList<>();

            if (!bossAddInfo.isEmpty()) {
                for (int i = 0; i < bossAddInfo.tagCount(); i++) {
                    NBTTagCompound addInfoTag = bossAddInfo.getCompoundTagAt(i);
                    BossAddInfo addInfo = new BossAddInfo();
                    addInfo.loadFromNBTData(addInfoTag);

                    spawnInfo.bossAddInfo.add(addInfo);
                }
            }
        }

        return spawnInfo;
    }
}