package io.github.ninjaphenix.torcherino.block;

import io.github.ninjaphenix.torcherino.tiles.TileTorcherino;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockLanterino extends BlockCarvedPumpkin implements ITileEntityProvider
{
    private int SPEED;
    public BlockLanterino()
    {
        super(Block.Builder.create(Material.GOURD, MapColor.ADOBE).hardnessAndResistance(1.0F, 1.0F).sound(SoundType.WOOD).lightValue(15));
    }
    public BlockLanterino(int speed)
    {
        this();
        SPEED = speed;
    }
    @Override
    public boolean onBlockActivated(IBlockState blockState, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float relX, float relY, float relZ)
    {
        if(hand == EnumHand.OFF_HAND) return true;
        TileEntity tile = world.getTileEntity(pos);
        if(tile == null || !(tile instanceof TileTorcherino)) return true;
        if(!world.isRemote)
        {
            TileTorcherino torch = (TileTorcherino) tile;
            torch.changeMode(player.isSneaking());
            player.sendStatusMessage(torch.getDescription(), true);
        }
        return true;
    }
    @Override
    public TileEntity createNewTileEntity(IBlockReader var1)
    {
        return new TileTorcherino(SPEED);
    }

    @Override
    public void tick(IBlockState blockState, World world, BlockPos pos, Random rand)
    {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof ITickable)
        {
            ((ITickable) te).update();
        }
    }

    @Override
    public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState state1, boolean b) {
        if (state.getBlock() != state1.getBlock()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            tileEntity.invalidate();
            super.onReplaced(state, world, pos, state1, b);
        }
    }

}

