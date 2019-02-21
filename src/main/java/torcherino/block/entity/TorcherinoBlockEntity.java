package torcherino.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import torcherino.Torcherino;
import torcherino.Utils;
import java.util.Random;

public class TorcherinoBlockEntity extends BlockEntity implements Tickable
{
    private boolean poweredByRedstone;
    private byte MODES = 4;
    private int speed, maxSpeed;
    private byte cachedMode, mode;
    private int xMin, yMin, zMin;
    private int xMax, yMax, zMax;
    private Random rand;

    public TorcherinoBlockEntity() { super(Torcherino.TORCHERINO_BLOCK_ENTITY_TYPE); rand = new Random(); }

    public TorcherinoBlockEntity(int speed) { this(); maxSpeed = speed; }

    public void tick()
    {
        if(world.isClient) return;
        if(poweredByRedstone || mode == 0 || speed == 0) return;
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

    public void setPoweredByRedstone(boolean powered) { poweredByRedstone = powered; }

    public void setSpeed(int sp) { speed = sp > maxSpeed ? maxSpeed : sp < 0 ? 0 : sp; }

    public void setMode(byte mo) { mode = mo > MODES ? MODES : mo < 0 ? 0 : mo; }

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
    public BlockEntityUpdateS2CPacket toUpdatePacket()
    {
        return new BlockEntityUpdateS2CPacket(getPos(), 126, toTag(new CompoundTag()));
    }
}
