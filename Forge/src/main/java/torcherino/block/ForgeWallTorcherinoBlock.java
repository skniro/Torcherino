package torcherino.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
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

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings({"deprecation"})
public final class ForgeWallTorcherinoBlock extends ForgeTorcherinoBlock implements EntityBlock, TierSupplier {
    public static final EnumProperty<Direction> FACING;
    protected static final float AABB_OFFSET = 2.5F;
    private static final Map<Direction, VoxelShape> AABBS;
    private final ResourceLocation tierID;

    public ForgeWallTorcherinoBlock(Properties properties, ResourceLocation tier) {
        super(properties, tier);
        this.tierID = tier;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState p_58152_, BlockGetter p_58153_, BlockPos p_58154_, CollisionContext p_58155_) {
        return getShape(p_58152_);
    }

    public static VoxelShape getShape(BlockState p_58157_) {
        return (VoxelShape)AABBS.get(p_58157_.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState p_58133_, LevelReader p_58134_, BlockPos p_58135_) {
        Direction $$3 = (Direction)p_58133_.getValue(FACING);
        BlockPos $$4 = p_58135_.relative($$3.getOpposite());
        BlockState $$5 = p_58134_.getBlockState($$4);
        return $$5.isFaceSturdy(p_58134_, $$4, $$3);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_58126_) {
        BlockState $$1 = this.defaultBlockState();
        LevelReader $$2 = p_58126_.getLevel();
        BlockPos $$3 = p_58126_.getClickedPos();
        Direction[] $$4 = p_58126_.getNearestLookingDirections();
        Direction[] var6 = $$4;
        int var7 = $$4.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            if ($$5.getAxis().isHorizontal()) {
                Direction $$6 = $$5.getOpposite();
                $$1 = (BlockState)$$1.setValue(FACING, $$6);
                if ($$1.canSurvive($$2, $$3)) {
                    return $$1;
                }
            }
        }

        return null;
    }

    @Override
    protected BlockState updateShape(BlockState p_58143_, LevelReader p_374329_, ScheduledTickAccess p_374207_, BlockPos p_58147_, Direction p_58144_, BlockPos p_58148_, BlockState p_58145_, RandomSource p_374234_) {
        return p_58144_.getOpposite() == p_58143_.getValue(FACING) && !p_58143_.canSurvive(p_374329_, p_58147_) ? Blocks.AIR.defaultBlockState() : p_58143_;
    }

    @Override
    public BlockState rotate(BlockState p_58140_, Rotation p_58141_) {
        return (BlockState)p_58140_.setValue(FACING, p_58141_.rotate((Direction)p_58140_.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState p_58137_, Mirror p_58138_) {
        return p_58137_.rotate(p_58138_.getRotation((Direction)p_58137_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58150_) {
        p_58150_.add(new Property[]{FACING});
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
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState state, boolean boolean_1) {
        this.neighborChanged(newState, level, pos, null, null, false);
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
        Direction $$4 = (Direction)$$0.getValue(FACING);
        double $$5 = (double)$$2.getX() + 0.5;
        double $$6 = (double)$$2.getY() + 0.7;
        double $$7 = (double)$$2.getZ() + 0.5;
        double $$8 = 0.22;
        double $$9 = 0.27;
        Direction $$10 = $$4.getOpposite();
        $$1.addParticle(ParticleTypes.SMOKE, $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
        ResourceLocation blockName = ForgeRegistries.BLOCKS.getKey($$0.getBlock());
        if (blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "wall_torcherino"))) {
            $$1.addParticle(TorcherinoParticleTypes.Normal_Torcherino_Flame.get(),$$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
        } else if(blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "wall_compressed_torcherino"))){
            $$1.addParticle(TorcherinoParticleTypes.Compressed_Torcherino_Flame.get(), $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
        }else if(blockName != null && blockName.equals(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "wall_double_compressed_torcherino"))){
            $$1.addParticle(TorcherinoParticleTypes.Double_Compressed_Torcherino_Flame.get(), $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
        }
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), Direction.SOUTH, Block.box(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), Direction.WEST, Block.box(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), Direction.EAST, Block.box(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));
    }
}
