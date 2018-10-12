package com.sci.torcherino.network;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyHandler
{
	public static KeyBinding usageKey;
	public static void preInit()
	{
		usageKey = new KeyBinding("key.torcherino.useage_key", Keyboard.KEY_LSHIFT, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(usageKey);
	}
}
