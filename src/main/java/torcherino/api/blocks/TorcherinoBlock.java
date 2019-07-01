package torcherino.api.blocks;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import torcherino.Utils;
import java.util.Random;

public class TorcherinoBlock extends TorchBlock implements BlockEntityProvider
{
	private final int MAX_SPEED;

	public TorcherinoBlock(int maxSpeed, Identifier id)
	{
		super(FabricBlockSettings.of(Material.PART).lightLevel(14).noCollision().sounds(BlockSoundGroup.WOOD).breakInstantly().drops(id).build());
		MAX_SPEED = maxSpeed;
	}

	@Override public PistonBehavior getPistonBehavior(BlockState state){ return PistonBehavior.IGNORE; }

	@Override public BlockEntity createBlockEntity(BlockView view){ return new TorcherinoBlockEntity(MAX_SPEED); }

	@Override public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState state, boolean boolean_1){ neighborUpdate(newState, world, pos, null, null, false); }

	@Override public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
	{
		if (world.isClient) return;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null) return;
		((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(world.isEmittingRedstonePower(pos.down(), Direction.DOWN));
	}

	@Override public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof Tickable) ((Tickable) blockEntity).tick();
	}

	@Override public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null) blockEntity.invalidate();
	}

	@Override public void onPlaced(World world, BlockPos pos, BlockState oldState, LivingEntity placer, ItemStack handStack)
	{
		if (world.isClient) return;
		String prefix = "Something";
		if (placer != null) prefix = placer.getDisplayName().getString() + " (" + placer.getUuidAsString() + ")";
		Utils.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix, StringUtils.capitalize(getTranslationKey().replace("blocks.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
	}

	@Override public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult)
	{
		if (world.isClient || hand == Hand.OFF_HAND) return true;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof TorcherinoBlockEntity)) return true;
		ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, Utils.getId("openscreen"), new PacketByteBuf(Unpooled.buffer()).writeCompoundTag(blockEntity.toTag(new CompoundTag())));
		return true;
	}
}
