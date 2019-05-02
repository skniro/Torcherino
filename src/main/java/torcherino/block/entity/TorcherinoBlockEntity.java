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
	private int randomTicks = 3, speed, MAX_SPEED, mode, redstoneInteractionMode;
	private Iterable<BlockPos> positions;
	private static final Random RANDOM = new Random();

	public TorcherinoBlockEntity(){ super(Torcherino.TORCHERINO_BLOCK_ENTITY_TYPE); }
	public TorcherinoBlockEntity(int speed){ this(); MAX_SPEED = speed; redstoneInteractionMode = 0; }

	public void setSpeed(int speed){ this.speed = MathHelper.clamp(speed, 0, MAX_SPEED); }
	public void setMode(int mode){ this.mode = MathHelper.clamp(mode, 0, 4); positions = BlockPos.iterate(pos.add(-mode, -1, -mode), pos.add(mode, 1, mode)); }
	public void setPoweredByRedstone(boolean powered){ poweredByRedstone = redstoneInteractionMode == 0 ? powered : redstoneInteractionMode == 1 && !powered; }
	@Override public BlockEntityUpdateS2CPacket toUpdatePacket(){ return new BlockEntityUpdateS2CPacket(getPos(), 126, toTag(new CompoundTag())); }

	@Override public void tick()
	{
		if (world.isClient || poweredByRedstone || mode == 0 || speed == 0) return;
		randomTicks = world.getGameRules().getInteger("randomTickSpeed");
		positions.forEach(this::tickBlock);
	}

	private void tickBlock(BlockPos pos)
	{
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == null || Utils.isBlockBlacklisted(block)) return;
		if (block.hasRandomTicks(blockState) && RANDOM.nextInt(4095) < randomTicks * speed) block.onRandomTick(blockState, world, pos, RANDOM);
		if (!block.hasBlockEntity()) return;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null || blockEntity.isInvalid() || !(blockEntity instanceof Tickable) || Utils.isBlockEntityBlacklisted(blockEntity.getType())) return;
		for (int i = 0; i < speed; i++) ((Tickable) blockEntity).tick();
	}

	public void setRedstoneInteractionMode(int mode)
	{
		redstoneInteractionMode = mode;
		BlockState state = world.getBlockState(pos);
		state.getBlock().neighborUpdate(state, world, pos, null, null, false);
	}

	@Override public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);
		tag.putInt("MaxSpeed", MAX_SPEED);
		tag.putInt("Speed", speed);
		tag.putInt("Mode", mode);
		tag.putInt("RedstoneInteractionMode", redstoneInteractionMode);
		tag.putBoolean("PoweredByRedstone", poweredByRedstone);
		return tag;
	}

	@Override public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		MAX_SPEED = tag.getInt("MaxSpeed");
		setSpeed(tag.getInt("Speed"));
		setMode(tag.getInt("Mode"));
		redstoneInteractionMode = tag.getInt("RedstoneInteractionMode");
		setPoweredByRedstone(tag.getBoolean("PoweredByRedstone"));
	}
}
