package torcherino.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import torcherino.Torcherino;
import torcherino.Utils;
import java.util.Random;

public class TorcherinoBlockEntity extends BlockEntity implements Tickable
{
    private static final String[] MODES = new String[]{"chat.torcherino.hint.area.stopped", "chat.torcherino.hint.area.n",
            "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n"};
    private boolean poweredByRedstone;
    private int speed, maxSpeed;
    private byte cachedMode, mode;
    private int xMin, yMin, zMin;
    private int xMax, yMax, zMax;
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
                block.onScheduledTick(blockState, world, pos, rand);
        if(!block.hasBlockEntity()) return;
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

    public TranslatableTextComponent getDescription()
    {
        return new TranslatableTextComponent("chat.torcherino.hint.layout",
                new TranslatableTextComponent(MODES[mode], 2*mode + 1),
                new TranslatableTextComponent("chat.torcherino.hint.speed",speed*100));
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
        return new BlockEntityUpdateClientPacket(getPos(), -999, toTag(new CompoundTag()));
    }

}
