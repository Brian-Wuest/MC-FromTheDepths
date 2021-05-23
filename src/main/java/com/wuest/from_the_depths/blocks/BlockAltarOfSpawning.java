package com.wuest.from_the_depths.blocks;

import com.wuest.from_the_depths.FromTheDepths;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.base.TileBlockBase;
import com.wuest.from_the_depths.tileentity.TileEntityAltarOfSpawning;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 
 * @author WuestMan
 *
 */
public class BlockAltarOfSpawning extends TileBlockBase<TileEntityAltarOfSpawning> {
  /**
   * The see through material for this block.
   */
  public static SeeThroughMaterial BlockMaterial = new SeeThroughMaterial(MapColor.AIR).setTranslucent(true);

  public static final PropertyDirection FACING = BlockHorizontal.FACING;

  /**
   * Initializes a new instance of the BlockAlterOfSpawning class.
   * 
   * @param name The name to register this block as.
   */
  public BlockAltarOfSpawning(String name) {
    super(BlockMaterial);
    this.setCreativeTab(FromTheDepths.CREATIVE_TAB);
    this.setSoundType(SoundType.STONE);

    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

    ModRegistry.setBlockName(this, name);
  }

  public static void SetBreakableStatus() {
    boolean allowAltarToBeDestroyed = FromTheDepths.proxy.getServerConfiguration().allowAltarToBeDestroyed;

    if (!allowAltarToBeDestroyed) {
      ModRegistry.AlterOfSpawning().setBlockUnbreakable();
      ModRegistry.AlterOfSpawning().setResistance(6000000.0F);
    } else {
      ModRegistry.AlterOfSpawning().setHardness(2);
      ModRegistry.AlterOfSpawning().setResistance(4.00000F);
    }
  }

  @Override
  public int tickRate(World worldIn) {
    return 20;
  }

  /**
   * Used to determine ambient occlusion and culling when rebuilding chunks for
   * render
   */
  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  /**
   * Determines if a torch can be placed on the top surface of this block. Useful
   * for creating your own block that torches can be on, such as fences.
   *
   * @param state The current state
   * @param world The current world
   * @param pos   Block position in world
   * @return True to allow the torch to be placed
   */
  @Override
  public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {
    return false;
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer()
  {
    return BlockRenderLayer.CUTOUT;
  }

  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }

  /**
   * Determines if this block can provide power.
   * 
   * @param state The block state (not used, can be null).
   */
  @Override
  public boolean canProvidePower(IBlockState state) {
    return false;
  }

  /**
   * Called after the block is set in the Chunk data, but before the Tile Entity
   * is set
   */
  @Override
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    this.setDefaultFacing(worldIn, pos, state);
  }

  private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
    if (!worldIn.isRemote) {
      IBlockState iblockstate = worldIn.getBlockState(pos.north());
      IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
      IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
      IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
      EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

      if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
        enumfacing = EnumFacing.SOUTH;
      } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
        enumfacing = EnumFacing.NORTH;
      } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
        enumfacing = EnumFacing.EAST;
      } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
        enumfacing = EnumFacing.WEST;
      }

      worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
    }
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to
   * allow for adjustments to the IBlockstate
   */
  @Override
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
      float hitZ, int meta, EntityLivingBase placer) {
    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  /**
   * Called by ItemBlocks after a block is set in the world, to allow post-place
   * logic
   */
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
      ItemStack stack) {
    worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  @SuppressWarnings("deprecation")
  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.byIndex(meta);

    if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
      enumfacing = EnumFacing.NORTH;
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  @Override
  public int getMetaFromState(IBlockState state) {
    return ((EnumFacing) state.getValue(FACING)).getIndex();
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If
   * inapplicable, returns the passed blockstate.
   */
  @Override
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If
   * inapplicable, returns the passed blockstate.
   */
  @Override
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { FACING });
  }

  @Override
  public int customUpdateState(World worldIn, BlockPos pos, IBlockState state, TileEntityAltarOfSpawning tileEntity) {
    return 0;
  }

  @Override
  public void customBreakBlock(TileEntityAltarOfSpawning tileEntity, World worldIn, BlockPos pos, IBlockState state) {
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    double d0 = (double) pos.getX() + 0.5D;
    double d1 = (double) pos.getY() + 1.05D;
    double d2 = (double) pos.getZ() + 0.05D;

    EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);

    switch (enumfacing) {
      case WEST: {
        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + 0.05D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.95D;
        d2 = (double) pos.getZ() + 0.50D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + .93D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        break;
      }

      case EAST: {
        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + 0.05D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.05D;
        d2 = (double) pos.getZ() + 0.50D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + .93D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        break;
      }

      case SOUTH: {
        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + 0.05D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.95D;
        d2 = (double) pos.getZ() + 0.50D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.05D;
        d2 = (double) pos.getZ() + 0.5D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        break;
      }

      default: {
        d0 = (double) pos.getX() + 0.05D;
        d2 = (double) pos.getZ() + 0.5D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.95D;
        d2 = (double) pos.getZ() + 0.50D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);

        d0 = (double) pos.getX() + 0.5D;
        d2 = (double) pos.getZ() + .93D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
      }
    }
  }

  /**
   * A simple transparent material which does block movement. The core
   * MaterialTransparent doesn't block movement.
   * 
   * @author WuestMan
   *
   */
  public static class SeeThroughMaterial extends Material {

    protected boolean translucent;
    protected boolean blocksMovement;

    public SeeThroughMaterial(MapColor color) {
      super(color);
      this.blocksMovement = true;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below
     * it if it returns true
     */
    @Override
    public boolean blocksLight() {
      return false;
    }

    public SeeThroughMaterial setTranslucent(boolean value) {
      this.translucent = value;
      return this;
    }

    /**
     * Indicate if the material is opaque
     */
    @Override
    public boolean isOpaque() {
      return this.translucent ? false : this.blocksMovement();
    }

    /**
     * Returns if this material is considered solid or not
     */
    @Override
    public boolean blocksMovement() {
      return this.blocksMovement;
    }

    /**
     * Sets the blocks movement field.
     * 
     * @param value The new value of the field.
     * @return This instance for property shortcuts.
     */
    public SeeThroughMaterial setBlocksMovement(boolean value) {
      this.blocksMovement = value;
      return this;
    }

    /**
     * Sets the immovable field.
     * 
     * @param value The new value of the field.
     * @return This instance for property shortcuts.
     */
    public SeeThroughMaterial setImmovable(boolean value) {
      return (SeeThroughMaterial) this.setImmovableMobility();
    }

  }
}