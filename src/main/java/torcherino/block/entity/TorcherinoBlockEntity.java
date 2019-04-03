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
	private boolean poweredByRedstone;
	private int speed, MAX_SPEED;
	private byte cachedMode, mode, redstoneInteractionMode;
	private int xMin, yMin, zMin;
	private int xMax, yMax, zMax;
	private static final Random RANDOM = new Random();

	public TorcherinoBlockEntity() { super(Torcherino.TORCHERINO_BLOCK_ENTITY_TYPE); }
	public TorcherinoBlockEntity(int speed) { this(); MAX_SPEED = speed; redstoneInteractionMode = 0; }

	public void setSpeed(int speed) { this.speed = MathHelper.clamp(speed, 0, MAX_SPEED); }
	public void setMode(byte mode) { this.mode = mode > 4 ? 4 : mode < 0 ? 0 : mode; }

	@Override public void tick()
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
		if(redstoneInteractionMode== 0) poweredByRedstone = powered;
		else if(redstoneInteractionMode== 1) poweredByRedstone = !powered;
		else if(redstoneInteractionMode== 2) poweredByRedstone = false;
	}

	public void setRedstoneInteractionMode(byte mode)
	{
		redstoneInteractionMode = mode;
		BlockState blockState = world.getBlockState(pos);
		blockState.getBlock().neighborUpdate(blockState, world, pos, null, null, false);
	}

	@Override public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);
		tag.putInt("Speed", speed);
		tag.putInt("MaxSpeed", MAX_SPEED);
		tag.putByte("Mode", mode);
		tag.putByte("RedstoneInteractionMode", redstoneInteractionMode);
		tag.putBoolean("PoweredByRedstone", poweredByRedstone);
		return tag;
	}

	@Override public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		speed = tag.getInt("Speed");
		MAX_SPEED = tag.getInt("MaxSpeed");
		mode = tag.getByte("Mode");
		redstoneInteractionMode = tag.getByte("RedstoneInteractionMode");
		poweredByRedstone = tag.getBoolean("PoweredByRedstone");
	}

	@Override public BlockEntityUpdateS2CPacket toUpdatePacket()
	{
		return new BlockEntityUpdateS2CPacket(getPos(), 126, toTag(new CompoundTag()));
	}
}
