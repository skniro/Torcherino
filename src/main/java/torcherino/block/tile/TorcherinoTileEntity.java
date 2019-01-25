package torcherino.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import torcherino.Torcherino;
import torcherino.Utils;

import java.util.Random;

public class TorcherinoTileEntity extends TileEntity implements ITickable
{
    private static final String[] MODES = new String[]{"chat.torcherino.hint.area.stopped", "chat.torcherino.hint.area.n",
            "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n"};
    private boolean poweredByRedstone;
    private int speed, maxSpeed;
    private byte cachedMode, mode;
    private int xMin, yMin, zMin;
    private int xMax, yMax, zMax;
    private Random rand;

    private TorcherinoTileEntity(TileEntityType<?> type)
    {
        super(type);
        rand = new Random();
    }

    public TorcherinoTileEntity()
    {
        this(Torcherino.TORCHERINO);
    }

    public TorcherinoTileEntity(int speed)
    {
        this(Torcherino.TORCHERINO);
        maxSpeed = speed;
    }

    public void tick()
    {
        if(world.isRemote) return;
        if(poweredByRedstone || mode == 0 || speed == 0) return;
        updateCachedModeIfNeeded();
        tickNeighbors();
    }

    private void updateCachedModeIfNeeded()
    {
        if(cachedMode != mode)
        {
            xMin = pos.getX() - mode;
            xMax = pos.getX() + mode;
            yMin = pos.getY() - 1;
            yMax = pos.getY() + 1;
            zMin = pos.getZ() - mode;
            zMax = pos.getZ() + mode;
            cachedMode = mode;
        }
    }

    private void tickNeighbors()
    {
        for(int x = xMin; x <= xMax; x++)
            for(int y = yMin; y <= yMax; y++)
                for(int z = zMin; z <= zMax; z++)
                    tickBlock(new BlockPos(x, y, z));

    }

    private void tickBlock(BlockPos pos)
    {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        // todo: verify if this is actually needed
        if(block == null) return;
        if(Utils.isBlockBlacklisted(block)) return;
        if(block.getTickRandomly(blockState))
            for(int i = 0; i < speed; i++)
                block.tick(blockState, world, pos, rand);
        if(!block.hasTileEntity()) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity == null || tileEntity.isRemoved()) return;
        if(Utils.isTileEntityBlacklisted(tileEntity.getType())) return;
        if(!(tileEntity instanceof ITickable)) return;
        for(int i = 0; i < speed; i++)
        {
            if(tileEntity.isRemoved()) break;
            ((ITickable) tileEntity).tick();
        }
    }

    public void setPoweredByRedstone(boolean powered)
    {
        poweredByRedstone = powered;
    }

    public void changeMode(boolean modifier)
    {
        if(modifier)
            if(speed < maxSpeed) speed += maxSpeed / 4; else speed = 0;
        else
        if(mode < MODES.length - 1) mode++; else mode = 0;
    }

    public TextComponentTranslation getDescription()
    {
        return new TextComponentTranslation("chat.torcherino.hint.layout",
                new TextComponentTranslation(MODES[mode], 2*mode + 1),
                new TextComponentTranslation("chat.torcherino.hint.speed",speed*100));
    }

    @Override
    public NBTTagCompound write(NBTTagCompound  tag)
    {
        super.write(tag);
        tag.putInt("Speed", speed);
        tag.putInt("MaxSpeed", maxSpeed);
        tag.putByte("Mode", mode);
        tag.putBoolean("PoweredByRedstone", poweredByRedstone);
        return tag;
    }

    @Override
    public void read(NBTTagCompound  tag)
    {
        super.read(tag);
        speed = tag.getInt("Speed");
        maxSpeed = tag.getInt("MaxSpeed");
        mode = tag.getByte("Mode");
        poweredByRedstone = tag.getBoolean("PoweredByRedstone");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), -999, write(new NBTTagCompound()));
    }

}
