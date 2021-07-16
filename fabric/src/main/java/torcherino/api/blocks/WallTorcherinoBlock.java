package torcherino.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import torcherino.api.TierSupplier;
import torcherino.api.TorcherinoLogic;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.Random;

@SuppressWarnings({"deprecation"})
public class WallTorcherinoBlock extends WallTorchBlock implements EntityBlock, TierSupplier {
    private final ResourceLocation tierID;

    // todo: take block properties as argument
    public WallTorcherinoBlock(ResourceLocation tier, TorcherinoBlock torcherinoBlock, ParticleOptions particleEffect) {
        super(Properties.copy(Blocks.WALL_TORCH).dropsLike(torcherinoBlock), particleEffect);
        this.tierID = tier;
    }

    @Override
    public ResourceLocation getTier() {
        return tierID;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter level) {
        return new TorcherinoBlockEntity();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState state, boolean boolean_1) {
        this.neighborChanged(newState, level, pos, null, null, false);
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
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, neighborPos, boolean_1, (be) -> be.setPoweredByRedstone(
                level.hasSignal(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite()), state.getValue(BlockStateProperties.HORIZONTAL_FACING))));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }
}
