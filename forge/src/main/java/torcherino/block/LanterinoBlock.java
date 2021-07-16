package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Lantern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.StringUtils;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.block.tile.TorcherinoTileEntity;
import torcherino.config.Config;
import torcherino.network.Networker;

import javax.annotation.Nullable;
import java.util.Random;

public class LanterinoBlock extends Lantern implements TierSupplier
{
    private final ResourceLocation tierName;

    public LanterinoBlock(final ResourceLocation tierName)
    {
        super(Block.Properties.copy(Blocks.LANTERN));
        this.tierName = tierName;
    }

    private static boolean isEmittingStrongRedstonePower(final Level world, final BlockPos pos, final Direction direction)
    { return world.getBlockState(pos).getDirectSignal(world, pos, direction) > 0; }

    @Override
    public ResourceLocation getTierName() { return tierName; }

    @Override
    public boolean hasTileEntity(final BlockState state) { return true; }

    @Override
    public BlockEntity createTileEntity(final BlockState state, final BlockGetter world) { return new TorcherinoTileEntity(); }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

    @Override @SuppressWarnings("deprecation")
    public InteractionResult use(final BlockState state, final Level world, final BlockPos pos,
                                 final Player player, final InteractionHand hand, final BlockHitResult hit)
    {
        if (!world.isClientSide) { Networker.INSTANCE.openScreenServer(world, (ServerPlayer) player, pos); }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(final Level world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack)
    {
        if (world.isClientSide) { return; }
        if (stack.hasCustomHoverName())
        {
            final BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tile).setCustomName(stack.getDisplayName()); }
        }
        if (Config.INSTANCE.log_placement)
        {
            String prefix = "Something";
            if (placer != null) { prefix = placer.getDisplayName().getString() + "(" + placer.getStringUUID() + ")"; }
            Torcherino.LOGGER.info("[Torcherino] {} placed a {} at {} {} {}.", prefix,
                    StringUtils.capitalize(getDescriptionId().replace("block.torcherino.", "").replace("_", " ")), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override @SuppressWarnings("deprecation")
    public void tick(final BlockState state, final ServerLevel world, final BlockPos pos, final Random random)
    {
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tileEntity).tick(); }
    }

    @Override
    public PushReaction getPistonPushReaction(final BlockState state) { return PushReaction.IGNORE; }

    @Override @SuppressWarnings("deprecation")
    public void onPlace(final BlockState state, final Level world, final BlockPos pos, final BlockState oldState, final boolean b)
    {
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity) { ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(state.getValue(BlockStateProperties.POWERED)); }
    }

    @Override @SuppressWarnings("deprecation")
    public void neighborChanged(final BlockState state, final Level world, final BlockPos pos, final Block block, final BlockPos fromPos, final boolean b)
    {
        if (world.isClientSide) { return; }
        final BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TorcherinoTileEntity)
        {
            final boolean powered;
            if (state.getValue(BlockStateProperties.HANGING).equals(true)) { powered = world.hasSignal(pos.above(), Direction.UP); }
            else
            {
                powered = isEmittingStrongRedstonePower(world, pos.west(), Direction.WEST) ||
                        isEmittingStrongRedstonePower(world, pos.east(), Direction.EAST) ||
                        isEmittingStrongRedstonePower(world, pos.south(), Direction.SOUTH) ||
                        isEmittingStrongRedstonePower(world, pos.north(), Direction.NORTH);
            }
            if (state.getValue(BlockStateProperties.POWERED) != powered)
            {
                world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, powered));
                ((TorcherinoTileEntity) tileEntity).setPoweredByRedstone(powered);
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context)
    {
        final boolean powered;
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {state = defaultBlockState(); }
        if (state.getValue(BlockStateProperties.HANGING).equals(true)) { powered = context.getLevel().hasSignal(context.getClickedPos().above(), Direction.UP); }
        else
        {
            final Level world = context.getLevel();
            final BlockPos pos = context.getClickedPos();
            powered = isEmittingStrongRedstonePower(world, pos.west(), Direction.WEST) ||
                    isEmittingStrongRedstonePower(world, pos.east(), Direction.EAST) ||
                    isEmittingStrongRedstonePower(world, pos.south(), Direction.SOUTH) ||
                    isEmittingStrongRedstonePower(world, pos.north(), Direction.NORTH);
        }
        return state.setValue(BlockStateProperties.POWERED, powered);
    }
}
