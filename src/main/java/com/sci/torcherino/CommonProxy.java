package com.sci.torcherino;

import com.sci.torcherino.blocks.ModBlocks;
import com.sci.torcherino.network.PacketHandler;

public class CommonProxy
{
	public void preInit()
	{
		PacketHandler.preInit();
		ModBlocks.preInit();
	}
}
