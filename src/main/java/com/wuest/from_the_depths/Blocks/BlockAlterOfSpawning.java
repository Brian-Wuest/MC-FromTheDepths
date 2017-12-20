package com.wuest.from_the_depths.Blocks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

/**
 * 
 * @author WuestMan
 *
 */
public class BlockAlterOfSpawning extends TileBlockBase<TileEntityAltarOfSpawning>
{
	/**
	 * Initializes a new instance of the BlockAlterOfSpawning class.
	 * @param name The name to register this block as.
	 */
	public BlockAlterOfSpawning(String name)
	{
		super(Material.ROCK);
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
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!worldIn.isRemote)
    	{
    		ItemStack usedItem = playerIn.getHeldItem(hand);
    		
    		ItemStack offHand = playerIn.getHeldItem(EnumHand.OFF_HAND);
    		
    		if (offHand.getItem() == Items.SPAWN_EGG)
    		{
    			ItemMonsterPlacer spawnEgg = (ItemMonsterPlacer)offHand.getItem();
    			ResourceLocation testMonster = ItemMonsterPlacer.getNamedIdFrom(offHand);
    			
    			Entity entity = EntityList.createEntityByIDFromName(testMonster, worldIn);
    			NBTTagCompound testTag = entity.serializeNBT();
    			
    			try
				{
					File path = File.createTempFile("test", ".json");
					path.setWritable(true);
					Files.write(testTag.toString(), path, Charset.defaultCharset());
					FMLLog.log.warn("Data Written to:" + path.getAbsolutePath());
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		
    		if (usedItem != null && !usedItem.isEmpty() && usedItem.getItem() instanceof ItemTotemOfSpawning)
    		{
    			TileEntityAltarOfSpawning tileEntity = this.getLocalTileEntity(worldIn, pos);
    			
    			if (tileEntity.getConfig().currentSpawnInfo != null
    					&& !Strings.isNullOrEmpty(tileEntity.getConfig().currentSpawnInfo.key))
    			{
    				playerIn.sendMessage(new TextComponentString("Cannot spawn a monster at this time as additional monsters are going to be spawned. Please wait for all adds to be spawned."));
    				playerIn.sendMessage(new TextComponentString("Current time until spawning is complete: " + String.valueOf(tileEntity.getConfig().currentSpawnInfo.bossAddInfo.totalSpawnDuration / this.tickRate(worldIn)) + " seconds." ));
    				return true;
    			}
    			
    			// Found a totem of spawning. Spawn the associated entity.
    			ItemTotemOfSpawning totemOfSpawning = (ItemTotemOfSpawning)usedItem.getItem();
    			SpawnInfo entityInfo = totemOfSpawning.getSpawnInfoFromItemStack(usedItem);
    			
    			if (entityInfo != null)
    			{
    				EntityLiving entity = (EntityLiving)entityInfo.bossInfo.createEntityForWorld(worldIn, pos);
    				
    				if (entity != null)
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
    					
    					if (entityInfo.bossAddInfo != null)
    					{
    						// Save off the spawn information for this tile entity since adds need to be spawned.
    						tileEntity.markDirty();
    						entityInfo.bossAddInfo.spawnFrequency = entityInfo.bossAddInfo.spawnFrequency * this.tickRate(worldIn);
    						entityInfo.bossAddInfo.totalSpawnDuration = entityInfo.bossAddInfo.totalSpawnDuration * this.tickRate(worldIn);
    						
        					tileEntity.getConfig().currentSpawnInfo = entityInfo;
    					}
    					
    					return true;
    				}
    				else
    				{
    					playerIn.sendMessage(new TextComponentString("Entity with name of [" + entityInfo.bossInfo.name + "] and mod of [" + entityInfo.bossInfo.domain + "] was not found."));
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
}