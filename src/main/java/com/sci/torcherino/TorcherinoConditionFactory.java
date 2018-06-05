package com.sci.torcherino;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class TorcherinoConditionFactory implements IConditionFactory
{
	@Override
	public BooleanSupplier parse(JsonContext context, JsonObject json)
	{
		boolean value = JsonUtils.getBoolean(json , "value", true);
		String key = JsonUtils.getString(json, "type");
		if(key.equals(Torcherino.MOD_ID+":"+"torcherino_overpowered")) return () -> Torcherino.overPoweredRecipe == value;
		else if(key.equals(Torcherino.MOD_ID+":"+"compressed_torcherino_enabled")) return () -> Torcherino.compressedTorcherino == value;
		else if(key.equals(Torcherino.MOD_ID+":"+"double_compressed_torcherino_enabled")) return () -> Torcherino.doubleCompressedTorcherino == value;
		return () -> false;
	}
}