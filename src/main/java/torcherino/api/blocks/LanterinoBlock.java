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
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;
import torcherino.config.Config;

import java.util.Random;

public class LanterinoBlock extends LanternBlock implements BlockEntityProvider, TierSupplier
{
    private final Identifier tierID;

    public LanterinoBlock(Identifier tier)
    {
        super(FabricBlockSettings.copy(Blocks.LANTERN).build());
        this.tierID = tier;
    }

    private static boolean isEmittingStrongRedstonePower(World world, BlockPos pos, Direction direction)
    {
        BlockState state = world.getBlockState(pos);
        return state.getStrongRedstonePower(world, pos, direction) > 0;
    }

    @Override
    public Identifier getTier() { return tierID; }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new TorcherinoBlockEntity(); }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

    @Override
    public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState state, boolean boolean_1)
    {
        neighborUpdate(null, world, pos, null, null, false);
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random)
    {
        if (world.isClient) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) ((TorcherinoBlockEntity) blockEntity).tick();
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult)
    {
        if (world.isClient || hand == Hand.OFF_HAND) return true;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            ((TorcherinoBlockEntity) blockEntity).writeClientData(buffer);
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, new Identifier(Torcherino.MOD_ID, "ots"), buffer);
        }
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
    {
        if (world.isClient) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity)
        {
            if (state == null) return;
            if (state.get(Properties.HANGING).equals(true))
            {
                ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(world.isEmittingRedstonePower(pos.up(), Direction.UP));
            }
            else
            {
                boolean powered = isEmittingStrongRedstonePower(world, pos.west(), Direction.WEST) ||
                        isEmittingStrongRedstonePower(world, pos.east(), Direction.EAST) ||
                        isEmittingStrongRedstonePower(world, pos.south(), Direction.SOUTH) ||
                        isEmittingStrongRedstonePower(world, pos.north(), Direction.NORTH);
                ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(powered);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if (world.isClient) return;
        if (stack.hasCustomName())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TorcherinoBlockEntity) ((TorcherinoBlockEntity) blockEntity).setCustomName(stack.getName());
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = placer == null ? "Something" : placer.getDisplayName().getString() + "(" + placer.getUuidAsString() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {}, {}, {}.", prefix, Registry.BLOCK.getId(this), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
