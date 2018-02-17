package com.wuest.from_the_depths.Blocks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Base.TileBlockBase;
import com.wuest.from_the_depths.EntityInfo.SpawnInfo;
import com.wuest.from_the_depths.Items.ItemTotemOfSpawning;
import com.wuest.from_the_depths.TileEntities.TileEntityAltarOfSpawning;

import jline.internal.Log.Level;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author WuestMan
 *
 */
public class BlockAltarOfSpawning extends TileBlockBase<TileEntityAltarOfSpawning>
{
	/**
	 * The see through material for this block.
	 */
	public static SeeThroughMaterial BlockMaterial = new SeeThroughMaterial(MapColor.AIR).setTranslucent(true);
	
	/**
	 * Initializes a new instance of the BlockAlterOfSpawning class.
	 * @param name The name to register this block as.
	 */
	public BlockAltarOfSpawning(String name)
	{
		super(BlockMaterial);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setSoundType(SoundType.STONE);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
		
		ModRegistry.setBlockName(this, name);
	}
	
	@Override
	public int tickRate(World worldIn)
	{
		return 20;
	}
	
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
	/**
	 * Determines if a torch can be placed on the top surface of this block.
	 * Useful for creating your own block that torches can be on, such as fences.
	 *
	 * @param state The current state
	 * @param world The current world
	 * @param pos Block position in world
	 * @return True to allow the torch to be placed
	 */
	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}
	
	@Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
	/**
	 * Determines if this block can provide power.
	 * @param state The block state (not used, can be null).
	 */
	@Override
	public boolean canProvidePower(IBlockState state)
	{
		return false;
	}
	
    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!worldIn.isRemote)
    	{
    		ItemStack usedItem = playerIn.getHeldItem(hand);
    		
    		ItemStack offHand = playerIn.getHeldItem(EnumHand.OFF_HAND);
    		
    		if (usedItem != null && !usedItem.isEmpty() && usedItem.getItem() instanceof ItemTotemOfSpawning)
    		{
    			TileEntityAltarOfSpawning tileEntity = this.getLocalTileEntity(worldIn, pos);
    			
    			if (tileEntity.getConfig().currentSpawnInfo != null
    					&& !Strings.isNullOrEmpty(tileEntity.getConfig().currentSpawnInfo.key))
    			{
    				playerIn.sendMessage(new TextComponentString("Cannot spawn a monster at this time as additional monsters are going to be spawned. Please wait for all adds to be spawned."));
    				playerIn.sendMessage(new TextComponentString("Approximate time until spawning is complete: " + String.valueOf(tileEntity.getConfig().currentSpawnInfo.bossAddInfo.totalSpawnDuration / this.tickRate(worldIn)) + " seconds." ));
    				return true;
    			}
    			else
    			{
	    			// Found a totem of spawning and we are not currently spawning a previous set of mosnters. Initiate the spawning of the entity.
	    			ItemTotemOfSpawning totemOfSpawning = (ItemTotemOfSpawning)usedItem.getItem();
	    			SpawnInfo spawnInfo = totemOfSpawning.getSpawnInfoFromItemStack(usedItem);
	    			
	    			if (spawnInfo != null)
	    			{	
	    				if (spawnInfo.bossInfo.isValidEntity(worldIn))
	    				{
	    					// Entity was spawned, update the itemstack.
	    					if (usedItem.getCount() == 1)
	    					{
	    						playerIn.inventory.deleteStack(usedItem);
	    					}
	    					else
	    					{
	    						usedItem.shrink(1);
	    					}
	    					
	    					playerIn.inventoryContainer.detectAndSendChanges();
	    					
    						// Save off the spawn information for this tile entity since adds need to be spawned.
    						tileEntity.InitiateSpawning(spawnInfo, this.tickRate(worldIn));
	    					
	    					return true;
	    				}
	    				else
	    				{
	    					playerIn.sendMessage(new TextComponentString("Entity with name of [" + spawnInfo.bossInfo.name + "] and mod of [" + spawnInfo.bossInfo.domain + "] was not found."));
	    				}
	    			}
    			}
    		}
    	}
    	
        return false;
    }

	@Override
	public int customUpdateState(World worldIn, BlockPos pos, IBlockState state, TileEntityAltarOfSpawning tileEntity)
	{
		return 0;
	}

	@Override
	public void customBreakBlock(TileEntityAltarOfSpawning tileEntity, World worldIn, BlockPos pos, IBlockState state)
	{
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double d0 = (double)pos.getX() + 0.5D;
        double d1 = (double)pos.getY() + 1.05D;
        double d2 = (double)pos.getZ() + 0.03D;

        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        
        d0 = (double)pos.getX() + 0.05D;
        d2 = (double)pos.getZ() + 0.50D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        
        d0 = (double)pos.getX() + 0.5D;
        d2 = (double)pos.getZ() + .93D;
        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
    }
	
    /**
     * A simple transparent material which does block movement.
     * The core MaterialTransparent doesn't block movement.
     * @author WuestMan
     *
     */
	public static class SeeThroughMaterial extends Material
	{

		protected boolean translucent;
		protected boolean blocksMovement;
		
		public SeeThroughMaterial(MapColor color)
		{
			super(color);
			this.blocksMovement = true;
		}
		
	    /**
	     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
	     */
	    @Override
		public boolean blocksLight()
	    {
	        return false;
	    }
	    
	    public SeeThroughMaterial setTranslucent(boolean value)
	    {
	    	this.translucent = value;
	    	return this;
	    }
	    
	    /**
	     * Indicate if the material is opaque
	     */
	    @Override
	    public boolean isOpaque()
	    {
	        return this.translucent ? false : this.blocksMovement();
	    }

	    /**
	     * Returns if this material is considered solid or not
	     */
	    @Override
	    public boolean blocksMovement()
	    {
	        return this.blocksMovement;
	    }
	    
	    /**
	     * Sets the blocks movement field.
	     * @param value The new value of the field.
	     * @return This instance for property shortcuts.
	     */
	    public SeeThroughMaterial setBlocksMovement(boolean value)
	    {
	    	this.blocksMovement = value;
	    	return this;
	    }
	    
	    /**
	     * Sets the immovable field.
	     * @param value The new value of the field.
	     * @return This instance for property shortcuts.
	     */
	    public SeeThroughMaterial setImmovable(boolean value)
	    {
	    	return (SeeThroughMaterial)this.setImmovableMobility();
	    }
		
	}
}