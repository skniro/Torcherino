package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.block.entity.CandleBlockEntity;

public class CandleBlock extends net.minecraft.world.level.block.CandleBlock implements EntityBlock, TierSupplier {
    private final ResourceLocation tierID;

    public CandleBlock(Properties properties, ResourceLocation tier) {
        super(properties);
        tierID = tier;
    }

    @Override
    public ResourceLocation getTier() {
        return tierID;
    }

    @NotNull
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CandleBlockEntity(Registry.BLOCK_ENTITY_TYPE.get(Torcherino.resloc("candle")), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return TorcherinoLogic.getTicker(level, state, type);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState state, boolean boolean_1) {
        this.neighborChanged(null, level, pos, null, null, false);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return TorcherinoLogic.onUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1) {
        TorcherinoLogic.<CandleBlockEntity>neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) ->
                be.setCandleLit(level.hasNeighborSignal(pos)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }
}
