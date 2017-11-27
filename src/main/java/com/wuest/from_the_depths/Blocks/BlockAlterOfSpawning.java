package com.wuest.from_the_depths.Blocks;

import com.wuest.from_the_depths.ModRegistry;
import com.wuest.from_the_depths.Items.ItemTotemOfSpawning;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * 
 * @author WuestMan
 *
 */
public class BlockAlterOfSpawning extends Block
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
	
    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!worldIn.isRemote)
    	{
    		ItemStack usedItem = playerIn.getHeldItem(hand);
    		
    		if (usedItem != null && !usedItem.isEmpty() && usedItem.getItem() instanceof ItemTotemOfSpawning)
    		{
    			// Found a totem of spawning. Spawn the associated entity.
    			ItemTotemOfSpawning totemOfSpawning = (ItemTotemOfSpawning)usedItem.getItem();
    			ResourceLocation entityInfo = totemOfSpawning.getEntityResourceNameFromItemStack(usedItem);
    			
    			if (entityInfo != null)
    			{
    				Entity entity = EntityList.createEntityByIDFromName(entityInfo, worldIn);
    				
    				if (entity != null)
    				{
    					entity.forceSpawn = true;
    					entity.setPositionAndUpdate(pos.getX(), pos.up(1).getY(), pos.getZ());
    					
    					worldIn.spawnEntity(entity);
    					
    					// Entity was spawned, update the itemstack.
    					if (usedItem.getCount() == 1)
    					{
    						playerIn.inventory.deleteStack(usedItem);
    					}
    					else
    					{
    						usedItem.setCount(usedItem.getCount() - 1);
    					}
    					
    					playerIn.inventoryContainer.detectAndSendChanges();
    					
    					return true;
    				}
    				else
    				{
    					playerIn.sendMessage(new TextComponentString("Entity with name of [" + entityInfo.getResourcePath() + "] and mod of [" + entityInfo.getResourceDomain() + "] was not found."));
    				}
    			}
    		}
    	}
    	
        return false;
    }
}