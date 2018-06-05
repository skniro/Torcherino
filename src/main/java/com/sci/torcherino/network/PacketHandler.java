package com.sci.torcherino.network;

import com.sci.torcherino.Torcherino;

import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler
{
	public static void preInit()
	{
		Torcherino.network.registerMessage(MessageModifierKey.class, MessageModifierKey.class, 0, Side.SERVER);
	}
	public static void sendUpdateToSever(boolean isKeyPressed)
	{
		Torcherino.network.sendToServer(new MessageModifierKey(isKeyPressed));
	}
}
