package com.sci.torcherino.blocks.blocks;

import com.sci.torcherino.Torcherino;
import com.sci.torcherino.blocks.tiles.TileTorcherino;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLanterino extends BlockPumpkin
{
	public BlockLanterino()
	{
		this.setHardness(1.0F);
		this.setSoundType(SoundType.WOOD);
        this.setLightLevel(1.0F);
        this.setUnlocalizedName("torcherino.lanterino");
    }
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        if(!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(pos);
            if(tile != null && tile instanceof TileTorcherino) ((TileTorcherino) tile).setPoweredByRedstone(world.isBlockIndirectlyGettingPowered(pos) > 0);
        }
        super.onBlockAdded(world, pos, state);
        if(Torcherino.logPlacement) Torcherino.logger.info(this.getClass().getName().substring(30) + " was placed at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
    }
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if(!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(pos);
            if(tile != null && tile instanceof TileTorcherino) ((TileTorcherino) tile).setPoweredByRedstone(world.isBlockIndirectlyGettingPowered(pos) > 0);
        }
        super.neighborChanged(state, world, pos, block, fromPos);
    }
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(!world.isRemote)
        {
            if(hand != EnumHand.MAIN_HAND) return false;
            TileEntity tile = world.getTileEntity(pos);
            if(tile == null || !(tile instanceof TileTorcherino)) return false;
            TileTorcherino torch = (TileTorcherino) tile;
            if(Torcherino.keyStates.get(player) == null) torch.changeMode(false);
            else torch.changeMode(Torcherino.keyStates.get(player).booleanValue()==true);
            player.sendStatusMessage(torch.getDescription(), true);
        }
        return false;
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state){return true;}
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state){return new TileTorcherino();}
}