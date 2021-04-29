package com.wuest.from_the_depths.capabilities;

/**
 * Transfers property data from one capability to another.
 * @author WuestMan
 *
 */
public interface ITransferable<T extends ITransferable<?>>
{
	/**
	 * Transfers properties from one transferable to another.
	 * @param transferable The other transferable to get the properties from.
	 */
	void transfer(T transferable);
}
