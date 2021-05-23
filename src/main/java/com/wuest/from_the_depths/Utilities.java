package com.wuest.from_the_depths;

import com.wuest.from_the_depths.base.Triple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class Utilities {

	// --- RoTN Edition BEGIN --- //

	/**
	 * Utility Method to localize an unlocalized translation key from .lang files
	 * @param unlocalized the translation key
	 * @return localized Version
	 */
	public static String localize(String unlocalized, Object... formatArgs) {
		return new TextComponentTranslation(unlocalized, formatArgs).getFormattedText();
	}

	/**
	 * @param string The String to capitalize
	 * @return {@code string} with the first letter uppercased.
	 */
	public static String capitalize(String string) {
		if (Character.isUpperCase(string.charAt(0)))
			return string;

		char uppercased = Character.toUpperCase(string.charAt(0));
		return uppercased + string.substring(1);
	}

	// --- RoTN Edition END --- //

	public static Triple<Boolean, BlockPos, BlockPos> isSpaceAroundAltarAir(BlockPos altarPos, World world) {
		boolean returnValue = true;
		int radius = FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius;
		int height = FromTheDepths.proxy.getServerConfiguration().altarSpawningHeight;

		BlockPos corner1 = altarPos.north(radius).east(radius).up(height);
		BlockPos corner2 = altarPos.south(radius).west(radius);

		for (BlockPos pos : BlockPos.getAllInBox(corner1, corner2)) {
			if (pos.getX() == altarPos.getX() && pos.getY() == altarPos.getY() && pos.getZ() == altarPos.getZ()) {
				continue;
			}

			if (!world.isAirBlock(pos)) {
				returnValue = false;
			}
		}

		return new Triple<>(returnValue, corner1, corner2);
	}

	public static Triple<Boolean, BlockPos, BlockPos> isGroundUnderAltarSolid(BlockPos altarPos, World world, int radius) {
		boolean returnValue = true;

		BlockPos corner1 = altarPos.north(radius).east(radius).down();
		BlockPos corner2 = altarPos.south(radius).west(radius).down();

		for (BlockPos pos : BlockPos.getAllInBox(corner1, corner2)) {
			if (pos.getX() == altarPos.getX() && pos.getY() == altarPos.getY() && pos.getZ() == altarPos.getZ()) {
				continue;
			}

			IBlockState state = world.getBlockState(pos);

			if (!state.isBlockNormalCube()) {
				returnValue = false;
			}
		}

		return new Triple<>(returnValue, corner1, corner2);
	}
}
