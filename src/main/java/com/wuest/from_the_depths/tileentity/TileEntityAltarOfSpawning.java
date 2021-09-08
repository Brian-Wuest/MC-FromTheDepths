package com.wuest.from_the_depths.tileentity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.base.TileEntityBase;
import com.wuest.from_the_depths.config.ConfigTileEntityAltarOfSpawning;
import com.wuest.from_the_depths.entityinfo.BossAddInfo;
import com.wuest.from_the_depths.entityinfo.SpawnInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("Guava")
public class TileEntityAltarOfSpawning extends TileEntityBase<ConfigTileEntityAltarOfSpawning> {

  private Predicate<EntityPlayerMP> validPlayer = input -> false;

  /**
   * The command sender to use when issuing commands when spawning monsters.
   */
  public ICommandSender commandSender = new ICommandSender() {

    @Override
    public MinecraftServer getServer() {
      return TileEntityAltarOfSpawning.this.world.getMinecraftServer();
    }

    @Nonnull
    @Override
    public String getName() {
      return "@";
    }

    @Nonnull
    @Override
    public World getEntityWorld() {
      return TileEntityAltarOfSpawning.this.world;
    }

    @Override
    public boolean canUseCommand(int permLevel, @Nonnull String commandName) {
      return permLevel <= 2;
    }
  };

  public TileEntityAltarOfSpawning() {
    super();
    this.config = new ConfigTileEntityAltarOfSpawning();
  }

  @Override
  public void setPos(BlockPos posIn) {
    this.pos = posIn.toImmutable();
    this.getConfig().pos = this.pos;
    validPlayer = Predicates.and(EntitySelectors.IS_ALIVE, EntitySelectors.withinRange(pos.getX(), pos.getY(), pos.getZ(), 15.0D));
  }

  /**
   * For tile entities, ensures the chunk containing the tile entity is saved to
   * disk later - the game won't think it hasn't changed and skip it.
   */
  @Override
  public void markDirty() {
    super.markDirty();

    if (!this.world.isRemote) {
      //if (config != null && config.currentSpawnInfo != null)
      //  System.out.println(config.currentSpawnInfo.bossInfo);
      MinecraftServer server = this.world.getMinecraftServer();
      server.getPlayerList().sendPacketToAllPlayers(this.getUpdatePacket());
    }
  }

  /**
   * Get the formatted ChatComponent that will be used for the sender's username
   * in chat
   */
  @Nullable
  @Override
  public ITextComponent getDisplayName() {
    if (this.getConfig().currentSpawnInfo != null && FromTheDepths.proxy.getServerConfiguration().showAltarSpawningText) {
      String display = "Summoning Monsters...";

      return new TextComponentString(display);
    }

    return null;
  }

  /**
   * Like the old updateEntity(), except more generic.
   */
  @Override
  public void update() {
    // When this is a peaceful world, don't allow this process to continue.
    if (!this.world.isRemote && this.world.getDifficulty() != EnumDifficulty.PEACEFUL
        && this.config.currentSpawnInfo != null) {

      if (!config.aliveMonsterIds.isEmpty())
        config.aliveMonsterIds.removeIf(id -> {
          Entity entity = ((WorldServer)world).getEntityFromUuid(id);
          return entity == null || entity.isDead;
        });

      if (!this.config.bossSpawned && this.config.preBossMinions.size() == 0) {

        if (this.config.totalLightningBolts >= 4) {
          //Spawn the actual boss
          Entity entity = this.config.currentSpawnInfo.bossInfo.createEntityForWorld(this.world, this.pos, validPlayer, this.commandSender);
          config.aliveMonsterIds.add(entity.getUniqueID());
          //System.out.println("added " + entity.getDisplayName().getFormattedText() + " to the list");

          if (entity == null) {
            TextComponentTranslation component = new TextComponentTranslation(
                    "from_the_depths.messages.invalid_arena",
                    FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius,
                    FromTheDepths.proxy.getServerConfiguration().altarSpawningHeight
            );

            this.world.getPlayers(EntityPlayerMP.class, validPlayer).forEach(player -> player.sendMessage(component));
          }

          this.config.bossSpawned = true;
          this.markDirty();

          // The boss has been spawned; make sure that there are no post-boss minions to
          // spawn.
          if (this.config.currentSpawnInfo.bossAddInfo == null || this.config.currentSpawnInfo.bossAddInfo.isEmpty()) {
            // There are no more adds to spawn since there were none to begin with.
            this.resetSpawner();
          } else {
            boolean foundPostBossMinions = false;

            for (BossAddInfo minion : this.config.currentSpawnInfo.bossAddInfo) {
              // Set the wait timer to be the number of ticks to wait after the boss is
              // spawned.
              // TODO: what's going on here
              foundPostBossMinions = true;
            }

            if (!foundPostBossMinions) {
              // No more adds to spawn, stop processing.
              this.resetSpawner();
            }
          }

          this.markDirty();
        } else if (this.config.ticksUntilNextLightningBolt - 1 <= 0) {
          EnumFacing facing = EnumFacing.NORTH;

          switch (this.config.totalLightningBolts) {
            case 0: {
              facing = EnumFacing.NORTH;
              break;
            }

            case 1: {
              facing = EnumFacing.EAST;
              break;
            }

            case 2: {
              facing = EnumFacing.SOUTH;
              break;
            }

            case 3: {
              facing = EnumFacing.WEST;
              break;
            }
          }

          BlockPos lightningBoltPos = this.pos.offset(facing, 2);
          world.addWeatherEffect(new EntityLightningBolt(world, lightningBoltPos.getX(), lightningBoltPos.getY(), lightningBoltPos.getZ(), true));

          this.config.totalLightningBolts++;
          this.config.ticksUntilNextLightningBolt = ModRegistry.AlterOfSpawning().tickRate(this.world);

          // Don't use this class's mark dirty method as it will send too many packets to
          // players.
          super.markDirty();
        } else {
          this.config.ticksUntilNextLightningBolt--;

          // Don't use this class's mark dirty method as it will send too many packets to
          // players.
          super.markDirty();
        }
      } else if (this.config.preBossMinions.size() > 0) {
        // Spawn the pre-boss minions.
        super.markDirty();

        for (int i = 0; i < this.config.preBossMinions.size(); i++) {
          BossAddInfo minion = this.config.preBossMinions.get(i);

          if (minion.processMinionSpawning(this.world, this.pos, this.commandSender, this.config.aliveMonsterIds)) {
            // This minion and all defined waves are done spawning, remove it from the list.
            this.config.preBossMinions.remove(i);
            i--;
          }
        }

        // Mark this tile entity as dirty after processing the loop.
        // This *could* cause some monster changes to be lost if the server dies before
        // the changes can be saved.
        // But this is more effiecient.
        this.markDirty();
      } else if (this.config.currentSpawnInfo.bossAddInfo != null) {

        for (int i = 0; i < this.config.currentSpawnInfo.bossAddInfo.size(); i++) {
          BossAddInfo minion = this.config.currentSpawnInfo.bossAddInfo.get(i);

          if (minion.processMinionSpawning(this.world, this.pos, this.commandSender, this.config.aliveMonsterIds)) {
            // This minion and all defined waves are done spawning, remove it from the list.
            this.config.currentSpawnInfo.bossAddInfo.remove(i);
            i--;
          }
        }

        // All adds have been generated. Reset the spawner so more can be spawned.
        if (this.config.currentSpawnInfo.bossAddInfo.size() == 0) {
          this.resetSpawner();
        }

        // Mark this tile entity as dirty after processing the loop.
        // This *could* cause some monster changes to be lost if the server dies before
        // the changes can be saved.
        // But this is more effiecient.
        this.markDirty();
      }
    }
  }

  public void resetSpawner() {
    if (!FromTheDepths.proxy.getServerConfiguration().canSpawnMultipleBosses && !config.aliveMonsterIds.isEmpty())
      return;

    this.config.totalLightningBolts = 0;
    this.config.ticksUntilNextLightningBolt = 100;
    this.config.currentSpawnInfo = null;
    this.config.bossSpawned = false;
    this.config.preBossMinions = new ArrayList<>();
    //System.out.println("SPAWNER WAS RESET!");
  }

  public void InitiateSpawning(SpawnInfo spawnInfo, int tickRate, World world) {
    this.resetSpawner();

    this.config.ticksUntilNextLightningBolt = tickRate;
    this.config.currentSpawnInfo = spawnInfo;

    //No entity has been spawned yet -> Boss Spawn Warning Message
    TextComponentString warningMessage = new TextComponentString(config.currentSpawnInfo.bossInfo.warningMessage);
    this.world.getPlayers(EntityPlayerMP.class, validPlayer).forEach(player -> player.sendMessage(warningMessage));

    // Get the pre-boss minions.
    if (spawnInfo.bossAddInfo != null) {
      for (int i = 0; i < spawnInfo.bossAddInfo.size(); i++) {
        BossAddInfo minion = spawnInfo.bossAddInfo.get(i);

        // Determine how many minions to spawn.
        minion.determineNumberToSpawn(world);

        if (minion.spawnBeforeBoss) {
          this.config.preBossMinions.add(minion);

          spawnInfo.bossAddInfo.remove(i);
          i--;
        }
      }
    }

    // Make sure to mark this tile entity as dirty since it's configuration changed
    // as part of this processing.
    this.markDirty();
  }
}