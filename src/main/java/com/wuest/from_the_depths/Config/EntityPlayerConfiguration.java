package com.wuest.from_the_depths.Config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author WuestMan This class serves as configuration data for the current
 *         player. It is expected that this lives on the client-side but saved
 *         on server side.
 */
public class EntityPlayerConfiguration {
  public static final String PLAYER_ENTITY_TAG = "FromTheDepthsTag";

  public EntityPlayerConfiguration() {
  }

  /**
   * Creates an EntityPlayerConfiguration from an entity player object.
   * 
   * @param player The player to create the instance from.
   * @return A new instance of EntityPlayerConfiguration.
   */
  public static EntityPlayerConfiguration loadFromEntityData(EntityPlayer player) {
    EntityPlayerConfiguration config = new EntityPlayerConfiguration();
    NBTTagCompound compoundTag = config.getModIsPlayerNewTag(player);

    config.loadFromNBTTagCompound(compoundTag);

    return config;
  }

  /**
   * Loads specific properties from saved NBTTag data.
   * 
   * @param tag The tag to load the data from.
   */
  public void loadFromNBTTagCompound(NBTTagCompound tag) {
  }

  /**
   * Gets and possibly creates the player tag used by this mod.
   * 
   * @param player The player to get the tag for.
   * @return An NBTTagCompound to save data too.
   */
  public NBTTagCompound getModIsPlayerNewTag(EntityPlayer player) {
    NBTTagCompound tag = player.getEntityData();

    // Get/create a tag used to determine if this is a new player.
    NBTTagCompound newPlayerTag = null;

    if (tag.hasKey(EntityPlayerConfiguration.PLAYER_ENTITY_TAG)) {
      newPlayerTag = tag.getCompoundTag(EntityPlayerConfiguration.PLAYER_ENTITY_TAG);
    } else {
      newPlayerTag = new NBTTagCompound();
      tag.setTag(EntityPlayerConfiguration.PLAYER_ENTITY_TAG, newPlayerTag);
    }

    return newPlayerTag;
  }

  /**
   * Saves this instance's data to the player tag.
   * 
   * @param player The player to save the data too.
   */
  public void saveToPlayer(EntityPlayer player) {
    NBTTagCompound compoundTag = this.getModIsPlayerNewTag(player);
  }

  /**
   * This is for clearing out non-persisted objects so when a player changes
   * worlds that the client-side config is cleared.
   */
  public void clearNonPersistedObjects() {
  }
}