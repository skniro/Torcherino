package com.sci.torcherino;

import com.sci.torcherino.blocks.ModBlocks;
import com.sci.torcherino.network.EventHandler;
import com.sci.torcherino.network.PacketHandler;

import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
	public void preInit()
	{
		PacketHandler.preInit();
		ModBlocks.preInit();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
}
