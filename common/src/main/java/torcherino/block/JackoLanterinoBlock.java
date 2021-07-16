package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import torcherino.api.TierSupplier;
import torcherino.block.TorcherinoLogic;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.Random;

@SuppressWarnings({"deprecation"})
public class JackoLanterinoBlock extends CarvedPumpkinBlock implements EntityBlock, TierSupplier {
    private final ResourceLocation tierID;

    // todo: take block properties as argument
    public JackoLanterinoBlock(ResourceLocation tier) {
        super(Properties.copy(Blocks.JACK_O_LANTERN));
        this.tierID = tier;
    }

    @Override
    public ResourceLocation getTier() {
        return tierID;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter view) {
        return new TorcherinoBlockEntity();
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
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        TorcherinoLogic.scheduledTick(state, level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return TorcherinoLogic.onUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1) {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) ->
                be.setPoweredByRedstone(level.hasNeighborSignal(pos)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }

    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    public BlockEntity createTileEntity(BlockState state, BlockGetter level) {
        return new TorcherinoBlockEntity();
    }
}
