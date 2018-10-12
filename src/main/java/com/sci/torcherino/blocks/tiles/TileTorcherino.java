package com.sci.torcherino.blocks.tiles;

import java.util.Random;

import com.sci.torcherino.Torcherino;
import com.sci.torcherino.TorcherinoRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

public class TileTorcherino extends TileEntity implements ITickable
{
    private static final String[] MODES = new String[]{"Stopped", "Area: 3x3x3", "Area: 5x3x5", "Area: 7x3x7", "Area: 9x3x9"};
	private static final int SPEEDS = 4;
    private boolean poweredByRedstone;
    private byte speed;
    private byte mode;
    private byte cachedMode;
    private Random rand;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;
    
    public TileTorcherino()
    {
        this.cachedMode = -1;
        this.rand = new Random();
    }
    
    protected int speed(int base){return base;}
    
    @Override
    public void update()
    {
        if(this.world.isRemote) return;
        if(this.poweredByRedstone || this.mode == 0 || this.speed == 0) return;
        this.updateCachedModeIfNeeded();
        this.tickNeighbors();
    }
    
    private void updateCachedModeIfNeeded()
    {
        if(this.cachedMode != this.mode)
        {
            this.xMin = this.pos.getX() - this.mode;
            this.yMin = this.pos.getY() - 1;
            this.zMin = this.pos.getZ() - this.mode;
            this.xMax = this.pos.getX() + this.mode;
            this.yMax = this.pos.getY() + 1;
            this.zMax = this.pos.getZ() + this.mode;
            this.cachedMode = this.mode;
        }
    }
    
    private void tickNeighbors()
    {
        for(int x = this.xMin; x <= this.xMax; x++)
        {
            for(int y = this.yMin; y <= this.yMax; y++)
            {
                for(int z = this.zMin; z <= this.zMax; z++)
                {
                    this.tickBlock(new BlockPos(x, y, z));
                }
            }
        }
    }
    
    private void tickBlock(BlockPos pos)
    {
    	IBlockState blockState = this.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(block == null || block instanceof BlockFluidBase || TorcherinoRegistry.isBlockBlacklisted(block))
        {
        	return;
        }
        if(block.getTickRandomly())
        {
            for(int i = 0; i < this.speed(this.speed); i++)
            {
                if(getWorld().getBlockState(pos) != blockState) break;
                block.updateTick(this.world, pos, blockState, this.rand);
            }
        }
        if(block.hasTileEntity(this.world.getBlockState(pos)))
        {
            TileEntity tile = this.world.getTileEntity(pos);
            if(tile == null || tile.isInvalid())
            {
            	return;
            }
            if(TorcherinoRegistry.isTileBlacklisted(tile.getClass())) return;
            for(int i = 0; i < this.speed(this.speed); i++)
            {
                if(tile.isInvalid())
                {
                	break;
                }
                if(tile instanceof ITickable)
                {
                	((ITickable) tile).update();
                }
            }
        }
    }
    
    public void setPoweredByRedstone(boolean poweredByRedstone)
    {
    	this.poweredByRedstone = poweredByRedstone;
    }
    
    public void changeMode(boolean modifier)
    {
        if(modifier)
        {
            if(this.speed < TileTorcherino.SPEEDS)
            {
                this.speed++;
            }
            else
            {
                this.speed = 0;
            }
        }
        else
        {
            if(this.mode < MODES.length - 1)
            {
                this.mode++;
            }
            else
            {
                this.mode = 0;
            }
        }
    }
    
    public TextComponentString getDescription()
    {
    	return new TextComponentString(TileTorcherino.MODES[this.mode] + " | Speed: " + this.speed(this.speed) * 100 + "%");
    }
    
    public String getMode()
    {
    	return TileTorcherino.MODES[this.mode];
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setByte("Speed", this.speed);
        tag.setByte("Mode", this.mode);
        tag.setBoolean("PoweredByRedstone", this.poweredByRedstone);
        return tag;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.speed = tag.getByte("Speed");
        this.mode = tag.getByte("Mode");
        this.poweredByRedstone = tag.getBoolean("PoweredByRedstone");
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), -999, nbt);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        this.readFromNBT(pkt.getNbtCompound());
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
}