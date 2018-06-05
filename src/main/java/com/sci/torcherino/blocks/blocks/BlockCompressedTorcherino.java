package com.sci.torcherino.blocks.blocks;
import com.sci.torcherino.blocks.tiles.TileCompressedTorcherino;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
public final class BlockCompressedTorcherino extends BlockTorcherino
{
    public BlockCompressedTorcherino(){this.setUnlocalizedName("torcherino.compressed_torcherino");}
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){return new TileCompressedTorcherino();}
}