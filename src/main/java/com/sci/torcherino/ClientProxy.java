package com.sci.torcherino;

import com.sci.torcherino.blocks.ModBlocks;
import com.sci.torcherino.network.KeyHandler;

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
