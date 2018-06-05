package com.sci.torcherino.blocks.blocks;

import com.sci.torcherino.blocks.tiles.TileDoubleCompressedTorcherino;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDoubleCompressedLanterino extends BlockLanterino
{
    public BlockDoubleCompressedLanterino(){this.setUnlocalizedName("torcherino.double_compressed_lanterino");}
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){return new TileDoubleCompressedTorcherino();}
}