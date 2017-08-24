package com.wuest.from_the_depths;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 * 
 * @author WuestMan
 *
 */
public class UpdateChecker
{
	/**
	 * Determines if a the message is shown to the user when they log into a world.
	 */
	public static boolean showMessage = false;
	
	/**
	 * The message to show to the user when they log into a world.
	 */
	public static String messageToShow = "";

	/**
	 * Checks the current version against the git-hub version.
	 */
	public static void checkVersion()
	{
		// Pull the repository information.
		ModContainer container = null;
		
		for (ModContainer modContainer : Loader.instance().getModList())
		{
			if (modContainer.getName().toLowerCase().equals(FromTheDepths.MODID.toLowerCase()))
			{
				container = modContainer;
				break;
			}
		}
		
		if (container != null)
		{
			CheckResult result = ForgeVersion.getResult(container);
			
			if (result != null && result.status == Status.OUTDATED)
			{
				// Current version is out dated, show the message when the user is logged in.
				UpdateChecker.messageToShow = "[From The Depths] There is a new version available! New Version: [" + result.target.toString() + "] Your Version: ["
						+ FromTheDepths.VERSION + "]";
				
				UpdateChecker.showMessage = true;
			}
		}
	}
}