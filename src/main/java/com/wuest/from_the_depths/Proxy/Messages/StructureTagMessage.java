package com.wuest.from_the_depths.Proxy.Messages;

import com.wuest.from_the_depths.Config.Structures.StructureConfiguration;
import com.wuest.from_the_depths.Config.*;
import com.wuest.from_the_depths.Config.Structures.*;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * 
 * @author WuestMan
 *
 */
public class StructureTagMessage extends TagMessage
{
	protected EnumStructureConfiguration structureConfig;
	
	/**
	 * Initializes a new instance of the StructureTagMessage class.
	 */
	public StructureTagMessage()
	{
	}

	/**
	 * Initializes a new instance of the StructureTagMessage class.
	 * @param tagMessage The message to send.
	 */
	public StructureTagMessage(NBTTagCompound tagMessage, EnumStructureConfiguration structureConfig)
	{
		super(tagMessage);
		
		this.structureConfig = structureConfig;
	}
	
	public EnumStructureConfiguration getStructureConfig()
	{
		return this.structureConfig;
	}
	
	public void setStructureConfig(EnumStructureConfiguration value)
	{
		this.structureConfig = value;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		// This class is very useful in general for writing more complex objects.
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		
		this.structureConfig = EnumStructureConfiguration.getFromIdentifier(tag.getInteger("config"));
		
		this.tagMessage = tag.getCompoundTag("dataTag");
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("config", this.structureConfig.identifier);
		tag.setTag("dataTag", this.tagMessage);
		
		ByteBufUtils.writeTag(buf, tag);
	}
	
	/**
	 * This enum is used to contain the structures which will be used in message handling.
	 * @author WuestMan
	 *
	 */
	public enum EnumStructureConfiguration
	{
		ChickenCoop(0, new ChickenCoopConfiguration());

		
		private <T extends StructureConfiguration> EnumStructureConfiguration(int identifier, T structureConfig)
		{
			this.identifier = identifier;
			this.structureConfig = structureConfig;
		}
		
		private int identifier;
		public StructureConfiguration structureConfig;
		
		public static EnumStructureConfiguration getFromIdentifier(int identifier)
		{
			for (EnumStructureConfiguration config : EnumStructureConfiguration.values())
			{
				if (config.identifier == identifier)
				{
					return config;
				}
			}
			
			return EnumStructureConfiguration.ChickenCoop;
		}
	}
}