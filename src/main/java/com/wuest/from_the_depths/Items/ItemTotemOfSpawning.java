package com.wuest.from_the_depths.Items;

import com.wuest.from_the_depths.ModRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author WuestMan
 *
 */
public class ItemTotemOfSpawning extends Item
{
	public NonNullList<ItemStack> subItems;
	public NonNullList<ItemStack> serverSubItems;
	
	public ItemTotemOfSpawning(String name)
	{
		super();
		
		this.subItems = NonNullList.create();
		this.serverSubItems = NonNullList.create();
		this.setCreativeTab(CreativeTabs.MATERIALS);
		ModRegistry.setItemName(this, name);
		this.setHasSubtypes(true);
	}
	
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			if (this.serverSubItems.size() > 0)
			{
				items.addAll(this.serverSubItems);
			}
			else if (this.subItems.size() > 0)
			{
				items.addAll(this.subItems);
			}
			else
			{
				super.getSubItems(tab, items);
			}
		}
	}

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
    	ResourceLocation resourceLocation = this.getEntityResourceNameFromItemStack(stack);
    	
    	if (resourceLocation != null)
    	{
    		String value = ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim() + " (" + resourceLocation.getResourcePath() + ")";
    		return value;
    	}
    	
        return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack))).trim();
    }
    
    /**
     * Override this method to change the NBT data being sent to the client.
     * You should ONLY override this when you have no other choice, as this might change behavior client side!
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Override
	public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
    	if (stack.getTagCompound() == null
    			|| stack.getTagCompound().hasNoTags())
    	{
    		// Make sure to serialize the NBT for this stack so the information is pushed to the client and the appropriate Icon is displayed for this stack.
    		stack.setTagCompound(stack.serializeNBT());
    	}
    	
        return stack.getTagCompound();
    }
    
    public ResourceLocation getEntityResourceNameFromItemStack(ItemStack stack)
    {
    	NBTTagCompound compound = this.getNBTShareTag(stack);
    	
    	if (compound != null && compound.hasKey("entityInfo"))
    	{
    		NBTTagCompound entityInfo = compound.getCompoundTag("entityInfo");
    		
    		String domain = entityInfo.getString("domain");
    		String name = entityInfo.getString("name");
    		
    		return new ResourceLocation(domain, name);
    	}
    	
    	return null;
    }
    
    public ItemStack getItemStackUsingEntityResourceName(ResourceLocation resourceLocation)
    {
    	ItemStack stack = new ItemStack(ModRegistry.TotemOfSpawning());
    	NBTTagCompound compound = this.getNBTShareTag(stack);
    	
    	NBTTagCompound entityInfo = new NBTTagCompound();
    	entityInfo.setString("domain", resourceLocation.getResourceDomain());
    	entityInfo.setString("name", resourceLocation.getResourcePath());
    	
    	compound.setTag("entityInfo", entityInfo);
    	stack.setTagCompound(compound);
    	
    	return stack;
    }
}