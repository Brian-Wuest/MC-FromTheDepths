package com.wuest.from_the_depths.events;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.blocks.BlockAltarOfSpawning;
import com.wuest.from_the_depths.config.ModConfiguration;
import com.wuest.from_the_depths.entityinfo.DropInfo;
import com.wuest.from_the_depths.proxy.ClientProxy;
import com.wuest.from_the_depths.proxy.CommonProxy;
import com.wuest.from_the_depths.proxy.messages.ConfigSyncMessage;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This is the server side event hander.
 * 
 * @author WuestMan
 */
@EventBusSubscriber(value = { Side.SERVER, Side.CLIENT })
public class ModEventHandler {
  /**
   * This event occurs when a player logs in. This is used to send server
   * configuration to the client.
   * 
   * @param event The event object.
   */
  @SubscribeEvent
  public static void onPlayerLoginEvent(PlayerLoggedInEvent event) {
    if (!event.player.world.isRemote) {
      NBTTagCompound tag = CommonProxy.proxyConfiguration.ToNBTTagCompound();
      FromTheDepths.network.sendTo(new ConfigSyncMessage(tag), (EntityPlayerMP) event.player);
      System.out.println("Sent config to '" + event.player.getDisplayNameString() + ".'");

      BlockAltarOfSpawning.SetBreakableStatus();
    }
  }

  /**
   * This event is used to clear out the server configuration for clients that log
   * off the server.
   * 
   * @param event The event object.
   */
  @SubscribeEvent
  public static void onPlayerLoggedOutEvent(PlayerLoggedOutEvent event) {
    // When the player logs out, make sure to re-set the server configuration.
    // This is so a new configuration can be successfully loaded when they switch
    // servers or worlds (on single player.
    if (event.player.world.isRemote) {
      // Make sure to null out the server configuration from the client.
      ((ClientProxy) FromTheDepths.proxy).serverConfiguration = null;
    }
  }

  /**
   * This is used to sync up the configuration when it's change by the user.
   * 
   * @param onConfigChangedEvent The event object.
   */
  @SubscribeEvent
  public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
    if (onConfigChangedEvent.getModID().equals(FromTheDepths.MODID)) {
      ModConfiguration.syncConfig();
    }
  }

  @SubscribeEvent
  public static void EntityDied(LivingDropsEvent event) {
    if (!event.getEntity().world.isRemote) {
      EntityLivingBase entity = event.getEntityLiving();
      NBTTagCompound entityCompoundTag = entity.getEntityData();

      if (entityCompoundTag.hasKey("from_the_depths")) {
        NBTTagCompound trackingTag = entityCompoundTag.getCompoundTag("from_the_depths");

        if (trackingTag.hasKey("additionalDrops")) {
          NBTTagList additionalDropList = trackingTag.getTagList("additionalDrops", 10);

          if (!additionalDropList.isEmpty()) {
            for (int i = 0; i < additionalDropList.tagCount(); i++) {
              NBTTagCompound dropInfoTag = additionalDropList.getCompoundTagAt(i);
              DropInfo dropInfo = new DropInfo();
              dropInfo.loadFromNBTData(dropInfoTag);

              EntityItem droppedItem = dropInfo.createEntityItem(entity.getEntityWorld(), entity.getPosition());

              if (droppedItem != null) {
                // Add a new dropped item to the list of items to drop.
                event.getDrops().add(droppedItem);
              }
            }
          }
        }
      }
    }
  }

  @SubscribeEvent
  public static void bossLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    final EntityLivingBase bossEntity = event.getEntityLiving();
    NBTTagCompound data = bossEntity.getEntityData();
    if (!data.hasKey("from_the_depths") || !data.getCompoundTag("from_the_depths").hasKey("timeUntilDespawn"))
      return;

    NBTTagCompound fromTheCompound = data.getCompoundTag("from_the_depths");

    //Should work correctly with time in seconds since we start checking this as soon as the bossEntity is spawned in the world
    if (bossEntity.ticksExisted % 20 == 0 && bossEntity instanceof EntityLiving) {

      EntityLivingBase targetEnt = ((EntityLiving) bossEntity).getAttackTarget();

      FromTheDepths.logger.info("Target: {}", targetEnt == null ? "null" : targetEnt.getDisplayName().getFormattedText());

      if (targetEnt == null) {
        if (!fromTheCompound.hasKey("idleTimeStart"))
          fromTheCompound.setLong("idleTimeStart", event.getEntity().world.getTotalWorldTime());
      }
      else {
        fromTheCompound.removeTag("idleTimeStart");
      }

      World world = bossEntity.world;
      int idleTimeUntilDespawn = fromTheCompound.getInteger("timeUntilDespawn");
      long idleTimeStart = fromTheCompound.getLong("idleTimeStart");
      BlockPos pos = BlockPos.fromLong(fromTheCompound.getLong("tilePos"));
      TileEntityAltarOfSpawning altar = (TileEntityAltarOfSpawning) world.getTileEntity(pos);

      //System.out.println(idleTimeStart + (idleTimeUntilDespawn * 20L) + "  -  " + world.getTotalWorldTime());
      if (idleTimeStart != 0 && idleTimeStart + (idleTimeUntilDespawn * 20L) <= world.getTotalWorldTime()) {
        if (altar != null)
          altar.resetSpawner();

        world.getPlayers(EntityPlayerMP.class, EntitySelectors.withinRange(bossEntity.posX, bossEntity.posY, bossEntity.posZ, 25)).forEach(player ->
          player.sendMessage(new TextComponentString("The boss has despawned after being idle for " + idleTimeUntilDespawn + " seconds."))
        );
        //world.getPlayers(EntityPlayerMP.class, );
        world.playSound(null, bossEntity.getPosition(), SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.HOSTILE, 1F, 1F);
        bossEntity.setDead();

        if (!world.isRemote) {
          WorldServer sWorld = (WorldServer) world;
          sWorld.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                  bossEntity.posX, bossEntity.posY + 0.5, bossEntity.posZ, 20,
                  Math.random(), Math.random(), Math.random(), Math.random() * 0.1 - 0.05);
        }
      }
    }
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(ModRegistry.ModBlocks.toArray(new Block[ModRegistry.ModBlocks.size()]));
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(ModRegistry.ModItems.toArray(new Item[ModRegistry.ModItems.size()]));
  }

  @SubscribeEvent
  public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
    // Register the ore dictionary blocks.
    ModRegistry.RegisterOreDictionaryRecords();
  }
}