package com.sci.torcherino.proxy;

import com.sci.torcherino.init.ModBlocks;

public final class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        ModBlocks.initRenders();
    }

    @Override
    public void init() {}

    @Override
    public void postInit() {}
}