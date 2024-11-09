package torcherino.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import torcherino.Torcherino;
import torcherino.api.TierSupplier;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.particle.TorcherinoParticleTypes;

@SuppressWarnings({"deprecation"})
public class ForgeTorcherinoBlock extends Block implements EntityBlock, TierSupplier {
    protected static final int AABB_STANDING_OFFSET = 2;
    protected static final VoxelShape AABB = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    private final ResourceLocation tierID;

    public ForgeTorcherinoBlock(Properties properties, ResourceLocation tier) {
        super(properties);
        tierID = tier;
    }

    @Override
    protected VoxelShape getShape(BlockState p_304673_, BlockGetter p_304919_, BlockPos p_304930_, CollisionContext p_304757_) {
        return AABB;
    }

    @Override
    protected BlockState updateShape(BlockState p_304418_, LevelReader p_374159_, ScheduledTickAccess p_374152_, BlockPos p_304633_, Direction p_304475_, BlockPos p_304603_, BlockState p_304669_, RandomSource p_374111_) {
        return p_304475_ == Direction.DOWN && !this.canSurvive(p_304418_, p_374159_, p_304633_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_304418_, p_374159_, p_374152_, p_304633_, p_304475_, p_304603_, p_304669_, p_374111_);
    }

    @Override
    public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
        return canSupportCenter(p_57500_, p_57501_.below(), Direction.UP);
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
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState state, boolean boolean_1) {
        this.neighborChanged(null, level, pos, null, null, false);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = InteractionHand.MAIN_HAND;
        return TorcherinoLogic.useWithoutItem(state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean boolean_1) {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, orientation, boolean_1, (be) ->
                be.setPoweredByRedstone(level.hasNeighborSignal(pos)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TorcherinoLogic.onPlaced(level, pos, state, placer, stack, this);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = (double)$$2.getX() + 0.5;
        double $$5 = (double)$$2.getY() + 0.7;
        double $$6 = (double)$$2.getZ() + 0.5;
        $$1.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
        ResourceLocation blockName = ForgeRegistries.BLOCKS.getKey($$0.getBlock());
        if (blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "torcherino"))) {
            $$1.addParticle(TorcherinoParticleTypes.Normal_Torcherino_Flame.get(), $$4, $$5, $$6, 0.0, 0.0, 0.0);
        } else if(blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "compressed_torcherino"))){
            $$1.addParticle(TorcherinoParticleTypes.Compressed_Torcherino_Flame.get(), $$4, $$5, $$6, 0.0, 0.0, 0.0);
        }else if(blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "double_compressed_torcherino"))){
            $$1.addParticle(TorcherinoParticleTypes.Double_Compressed_Torcherino_Flame.get(), $$4, $$5, $$6, 0.0, 0.0, 0.0);
        }
    }
}
