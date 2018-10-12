package com.sci.torcherino;

import com.sci.torcherino.blocks.ModBlocks;
import com.sci.torcherino.network.EventHandler;
import com.sci.torcherino.network.KeyHandler;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		super.preInit();
		ModBlocks.initRenders();
		KeyHandler.preInit();
		
	}
}
