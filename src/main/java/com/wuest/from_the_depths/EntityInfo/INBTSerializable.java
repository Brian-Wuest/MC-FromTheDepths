package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSerializable<T> {
  void writeToNBT(NBTTagCompound tag);
  
  T loadFromNBTData(NBTTagCompound nbtData);
}