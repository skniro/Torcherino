package com.sci.torcherino.block;

import com.sci.torcherino.tile.TileDoubleCompressedTorcherino;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class BlockDoubleCompresedTorcherino extends BlockTorcherino {
    public BlockDoubleCompresedTorcherino() {
        this.setUnlocalizedName("torcherino.double_compressed_torcherino");
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int i) {
        return new TileDoubleCompressedTorcherino();
    }
}