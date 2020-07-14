package com.wuest.from_the_depths.EntityInfo;

import net.minecraft.util.IStringSerializable;

public enum SpawnEffectEnum implements IStringSerializable {

	NONE("none"),
	LIGHTNING("lightning");

	private final String name;

	SpawnEffectEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static SpawnEffectEnum getFromName(String name) {
		SpawnEffectEnum returnValue = SpawnEffectEnum.LIGHTNING;

		if (name.equals(SpawnEffectEnum.NONE.getName())) {
			returnValue = SpawnEffectEnum.NONE;
		}

		return returnValue;
	}
}
