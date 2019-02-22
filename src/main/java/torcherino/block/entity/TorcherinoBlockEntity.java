package torcherino.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import torcherino.Torcherino;
import torcherino.Utils;
import java.util.Random;

public class TorcherinoBlockEntity extends BlockEntity implements Tickable
{
    public enum PowerState
    {
        NORMAL, INVERTED, IGNORE;

        public static PowerState fromByte(byte i)
        {
            switch(i)
            {
                case 0: return PowerState.NORMAL;
                case 1: return PowerState.INVERTED;
                case 2: return PowerState.IGNORE;
                default: return null;
            }
        }
    }

    private boolean poweredByRedstone;
    private PowerState redstonePowerMode;
    private int speed, MAX_SPEED;
    private byte cachedMode, mode;
    private int xMin, yMin, zMin;
    private int xMax, yMax, zMax;
    private final Random RANDOM;

    public TorcherinoBlockEntity()
    {
        super(Torcherino.TORCHERINO_BLOCK_ENTITY_TYPE);
        RANDOM= new Random();
        redstonePowerMode = PowerState.NORMAL;
    }

    public TorcherinoBlockEntity(int speed) { this(); MAX_SPEED = speed; }

    public void setSpeed(int speed) { this.speed = MathHelper.clamp(speed, 0, MAX_SPEED); }
    public void setMode(byte mode) { this.mode = mode > 4 ? 4 : mode < 0 ? 0 : mode; }

    public void tick()
    {
        if(world.isClient) return;
        if(poweredByRedstone || mode == 0 || speed == 0) return;
        if(cachedMode != mode)
        {
            xMin = pos.getX() - mode; yMin = pos.getY() - 1; zMin = pos.getZ() - mode;
            xMax = pos.getX() + mode; yMax = pos.getY() + 1; zMax = pos.getZ() + mode;
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
        	for(int i = 0; i < speed; i++) block.onScheduledTick(blockState, world, pos, RANDOM);
        if(!block.hasBlockEntity()) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity == null || blockEntity.isInvalid()) return;
        if(Utils.isBlockEntityBlacklisted(blockEntity.getType())) return;
        if(!(blockEntity instanceof Tickable)) return;
        if(blockEntity.isInvalid()) return;
        for(int i = 0; i < speed; i++) ((Tickable) blockEntity).tick();
    }

    public void setPoweredByRedstone(boolean powered)
    {
        if(redstonePowerMode == PowerState.NORMAL) poweredByRedstone = powered;
        else if(redstonePowerMode == PowerState.INVERTED) poweredByRedstone = !powered;
        else if(redstonePowerMode == PowerState.IGNORE) poweredByRedstone = false;
    }

    public void setRedstonePowerMode(PowerState state)
    {
        redstonePowerMode = state;
        BlockState blockState = world.getBlockState(pos);
        blockState.getBlock().neighborUpdate(blockState, world, pos, null, null);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);
        tag.putInt("Speed", speed);
        tag.putInt("MaxSpeed", MAX_SPEED);
        tag.putByte("Mode", mode);
        tag.putByte("RedstonePowerMode", new Integer(redstonePowerMode.ordinal()).byteValue());
        tag.putBoolean("PoweredByRedstone", poweredByRedstone);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);
        speed = tag.getInt("Speed");
	    MAX_SPEED = tag.getInt("MaxSpeed");
        mode = tag.getByte("Mode");
        redstonePowerMode = PowerState.fromByte(tag.getByte("RedstonePowerMode"));
        poweredByRedstone = tag.getBoolean("PoweredByRedstone");
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket()
    {
        return new BlockEntityUpdateS2CPacket(getPos(), 126, toTag(new CompoundTag()));
    }
}
