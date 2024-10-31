package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import torcherino.api.TierSupplier;
import torcherino.block.entity.TorcherinoBlockEntity;

public final class LanterinoBlock extends LanternBlock implements EntityBlock, TierSupplier {
    private final ResourceLocation tierID;

    public LanterinoBlock(Properties properties, ResourceLocation tier) {
        super(properties);
        this.tierID = tier;
    }

    private static boolean isEmittingStrongRedstonePower(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos).getDirectSignal(level, pos, direction) > 0;
    }

    @Override
    public ResourceLocation getTier() {
        return tierID;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TorcherinoBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TorcherinoLogic.getTicker(level, state, type);
    }

    @Deprecated
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState state, boolean boolean_1) {
        this.neighborChanged(null, level, pos, null, null, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = InteractionHand.OFF_HAND;
        return TorcherinoLogic.onUse(state, level, pos, player,hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1) {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) -> {
            if (state == null) {
                return;
            }
            if (state.getValue(BlockStateProperties.HANGING).equals(true)) {
                be.setPoweredByRedstone(level.hasSignal(pos.above(), Direction.UP));
            } else {
                boolean powered = isEmittingStrongRedstonePower(level, pos.west(), Direction.WEST) ||
                        isEmittingStrongRedstonePower(level, pos.east(), Direction.EAST) ||
                        isEmittingStrongRedstonePower(level, pos.south(), Direction.SOUTH) ||
                        isEmittingStrongRedstonePower(level, pos.north(), Direction.NORTH);
                be.setPoweredByRedstone(powered);
            }
        });

    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }
}
