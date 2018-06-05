package com.sci.torcherino.blocks.blocks;

import com.sci.torcherino.blocks.tiles.TileDoubleCompressedTorcherino;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class BlockDoubleCompressedTorcherino extends BlockTorcherino
{
    public BlockDoubleCompressedTorcherino(){this.setUnlocalizedName("torcherino.double_compressed_torcherino");}
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){return new TileDoubleCompressedTorcherino();}
}