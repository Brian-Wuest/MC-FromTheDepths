package com.wuest.from_the_depths;

import com.wuest.from_the_depths.Base.Triple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utilities {

	public static Triple<Boolean, BlockPos, BlockPos> isSpaceAroundAltarAir(BlockPos altarPos, World world) {
		boolean returnValue = true;
		int radius = FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius;
		int height = FromTheDepths.proxy.getServerConfiguration().altarSpawningHeight;

		BlockPos corner1 = altarPos.north(radius).east(radius).up(height);
		BlockPos corner2 = altarPos.south(radius).west(radius);

		for (BlockPos pos : BlockPos.getAllInBox(corner1,corner2)) {
			if (pos.getX() == altarPos.getX() && pos.getY() == altarPos.getY() && pos.getZ() == altarPos.getZ()) {
				continue;
			}

			if (!world.isAirBlock(pos)) {
				returnValue = false;
			}
		}

		return new Triple<>(returnValue, corner1, corner2);
	}

	public static Triple<Boolean, BlockPos, BlockPos> isGroundUnderAltarSolid(BlockPos altarPos, World world) {
		boolean returnValue = true;
		int radius = FromTheDepths.proxy.getServerConfiguration().altarSpawningRadius;

		BlockPos corner1 = altarPos.north(radius).east(radius).down();
		BlockPos corner2 = altarPos.south(radius).west(radius);

		for (BlockPos pos : BlockPos.getAllInBox(corner1,corner2)) {
			if (pos.getX() == altarPos.getX() && pos.getY() == altarPos.getY() && pos.getZ() == altarPos.getZ()) {
				continue;
			}

			IBlockState state = world.getBlockState(pos);

			if (!state.isSideSolid(world, pos, EnumFacing.UP)) {
				returnValue = false;
			}
		}

		return new Triple<>(returnValue, corner1, corner2);
	}
}
