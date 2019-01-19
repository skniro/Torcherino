package torcherino.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import torcherino.Torcherino;
import torcherino.Utils;
import java.util.Random;

public class TorcherinoBlockEntity extends BlockEntity implements Tickable
{
    private static final String[] MODES = new String[]{"Stopped", "Area: 3x3x3", "Area: 5x3x5", "Area: 7x3x7", "Area: 9x3x9"};
    private boolean poweredByRedstone;
    private int maxSpeed;
    private int speed;
    private byte mode;
    private byte cachedMode;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;
    private Random rand;

    public TorcherinoBlockEntity()
    {
        super(Torcherino.TorcherinoBlockEntity);
        rand = new Random();
    }

    public TorcherinoBlockEntity(int speed)
    {
        this();
        maxSpeed = speed;
    }

    public void tick()
    {
        if(world.isClient) return;
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
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(block == null) return;
        if(Utils.isBlockBlacklisted(block)) return;
        if(block.hasRandomTicks(blockState))
            for(int i = 0; i < speed; i++)
                block.scheduledTick(blockState, world, pos, rand);
        if(block.hasBlockEntity())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity == null || blockEntity.isInvalid()) return;
            if(Utils.isBlockEntityBlacklisted(blockEntity.getType())) return;
            if(!(blockEntity instanceof Tickable)) return;
            for(int i = 0; i < speed; i++)
            {
                if(blockEntity.isInvalid()) break;
                ((Tickable) blockEntity).tick();
            }
        }
    }

    public void setPoweredByRedstone(boolean powered)
    {
        poweredByRedstone = powered;
    }

    public void changeMode(boolean modifier)
    {
        if(modifier)
        {
            if(speed < maxSpeed)
                speed += maxSpeed / 4;
            else
                speed = 0;
        }
        else
        {
            if(mode < MODES.length - 1)
                mode++;
            else
                mode = 0;
        }
    }

    public StringTextComponent getDescription()
    {
        return new StringTextComponent(TorcherinoBlockEntity.MODES[mode] + " | Speed: " + speed*100 + "%");
    }

    public String getMode()
    {
        return TorcherinoBlockEntity.MODES[mode];
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);
        tag.putInt("Speed", speed);
        tag.putInt("MaxSpeed", maxSpeed);
        tag.putByte("Mode", mode);
        tag.putBoolean("PoweredByRedstone", poweredByRedstone);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);
        speed = tag.getInt("Speed");
        maxSpeed = tag.getInt("MaxSpeed");
        mode = tag.getByte("Mode");
        poweredByRedstone = tag.getBoolean("PoweredByRedstone");
    }

    @Override
    public BlockEntityUpdateClientPacket toUpdatePacket()
    {
        CompoundTag tag = new CompoundTag();
        toTag(tag);
        return new BlockEntityUpdateClientPacket(getPos(), -999, tag);
    }

}
