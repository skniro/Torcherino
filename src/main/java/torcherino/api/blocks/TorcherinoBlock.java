package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import torcherino.Torcherino;
import torcherino.config.Config;

import java.util.Random;

public class TorcherinoBlock extends TorchBlock implements BlockEntityProvider
{
    private final Identifier tierID;

    public TorcherinoBlock(Identifier tierID)
    {
        super(FabricBlockSettings.copy(Blocks.TORCH).build());
        this.tierID = tierID;
    }

    public Identifier getTierID() { return tierID; }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new TorcherinoBlockEntity(); }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

    @Override
    public void onBlockAdded(BlockState newState, World world, BlockPos pos, BlockState state, boolean boolean_1)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TorcherinoBlockEntity) ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(state.get(Properties.POWERED));
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) blockEntity.invalidate();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        boolean powered = context.getWorld().isReceivingRedstonePower(context.getBlockPos().down());
        return super.getPlacementState(context).with(Properties.POWERED, powered);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(Properties.POWERED);
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
            //Open our screen.
            //ServerSidePacketRegistryImpl.INSTANCE
            //        .sendToPlayer(player, Utils.getId("openscreen"), new PacketByteBuf(Unpooled.buffer()).writeCompoundTag(blockEntity.toTag(new CompoundTag())));
        }
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1)
    {
        if (world.isClient) return;
        boolean powered = world.isReceivingRedstonePower(pos.down());
        if (state.get(Properties.POWERED) != powered)
        {
            world.setBlockState(pos, state.with(Properties.POWERED, powered));
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TorcherinoBlockEntity)
            {
                ((TorcherinoBlockEntity) blockEntity).setPoweredByRedstone(world.isReceivingRedstonePower(pos));
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
            if (!(blockEntity instanceof TorcherinoBlockEntity)) return;
            ((TorcherinoBlockEntity) blockEntity).setCustomName(stack.getName());
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = placer == null ? "Something" : placer.getDisplayName().getString() + "(" + placer.getUuidAsString() + ")";
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {}, {}, {}.", prefix, Registry.BLOCK.getId(this), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
