package com.wuest.from_the_depths.EntityInfo;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.IStringSerializable;

public enum SpawnEffectEnum implements IStringSerializable {

	@SerializedName("none")
	NONE("none"),

	@SerializedName("lightning")
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
