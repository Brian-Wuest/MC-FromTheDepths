package com.wuest.from_the_depths.entityinfo;

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
		SpawnEffectEnum returnValue = SpawnEffectEnum.NONE;

		if (name != null && name.toLowerCase().equals(SpawnEffectEnum.LIGHTNING.getName())) {
			returnValue = SpawnEffectEnum.LIGHTNING;
		}

		return returnValue;
	}
}
