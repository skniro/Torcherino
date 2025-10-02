package torcherino.block;

import com.google.common.collect.BiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import torcherino.ModContent;
import torcherino.api.TierSupplier;
import torcherino.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CopperLanterinoBlock extends LanternBlock implements EntityBlock, TierSupplier {
    private final ResourceLocation tierID;
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> NEXT_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> PREVIOUS_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> WAXABLES_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> UNWAXABLES_BY_TIER = new HashMap<>();
/*
    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                             .put(ModContent.copperLantern.get(), ModContent.waxedCopper.get())
                             .put(ModContent.exposedCopper.get(), ModContent.waxedExposedCopper.get())
                             .put(ModContent.weatheredCopper.get(), ModContent.waxedWeatheredCopper.get())
                             .put(ModContent.oxidizedCopper.get(), ModContent.waxedOxidizedCopper.get()).build();
    });
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> {
        return WAXABLES.get().inverse();
    });

    public static final Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                             .put(ModContent.copperLantern.get(), ModContent.exposedCopper.get())
                             .put(ModContent.exposedCopper.get(), ModContent.weatheredCopper.get())
                             .put(ModContent.weatheredCopper.get(), ModContent.oxidizedCopper.get()).build();
    });
    public static final Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> ((BiMap)NEXT_BY_BLOCK.get()).inverse());
*/


    public CopperLanterinoBlock(Properties properties, ResourceLocation tier) {
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
        InteractionHand hand = InteractionHand.MAIN_HAND;
        return TorcherinoLogic.useWithoutItem(state, level, pos, player, hand, hit);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        boolean handled = false;

        if (item == Items.HONEYCOMB) {
            if (!level.isClientSide()) {
                BiMap<Block, Block> waxables = (BiMap<Block, Block>) LanterinoOxidizableRegistry.getWaxMap(tierID);
                Block waxed = waxables.get(state.getBlock());
                if (waxed != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, waxed.defaultBlockState());
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    level.levelEvent(player, 3003, pos, 0);
                    handled = true;
                }
            }
        }


        if (item instanceof AxeItem) {
            if (!level.isClientSide()) {
                BiMap<Block, Block> waxOffMap = (BiMap<Block, Block>) LanterinoOxidizableRegistry.getUnwaxMap(tierID);
                Block unwaxed = waxOffMap.get(state.getBlock());
                if (unwaxed != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, unwaxed.defaultBlockState());
                    stack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> {
                    });
                    level.levelEvent(player, 3005, pos, 0);
                    handled = true;
                }

                BiMap<Block, Block> prevMap = (BiMap<Block, Block>) LanterinoOxidizableRegistry.getPreviousByBlock(tierID);
                Block prev = prevMap.get(state.getBlock());
                if (prev != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, prev.defaultBlockState());
                    stack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> {
                    });
                    level.levelEvent(player, 3004, pos, 0);
                    handled = true;
                }
            }
        }

        return handled ? InteractionResult.SUCCESS : super.useItemOn(itemStack, state, level, pos, player, hand, hit);
    }

/*    public static BiMap<Block, Block> getWaxOff(ResourceLocation tierID) {
        return ModContent.WAXABLES.get(tierID).inverse();
    }

    public static BiMap<Block, Block> getPreviousByBlock(ResourceLocation tierID) {
        return ModContent.NEXT_BY_BLOCK.get(tierID).inverse();
    }*/

    public static void swapPreserveTorcherinoBE(ServerLevel level, BlockPos pos,
                                                BlockState oldState, BlockState newState) {
        BlockEntity oldBE = level.getBlockEntity(pos);
        TorcherinoBlockEntity.Data data = null;

        if (oldBE instanceof TorcherinoBlockEntity torcherino) {
            data = TorcherinoBlockEntity.Data.from(torcherino);
        }

        level.setBlock(pos, newState, Block.UPDATE_ALL);
        BlockEntity newBE = level.getBlockEntity(pos);

        if (data != null && newBE instanceof TorcherinoBlockEntity torcherino) {
            torcherino.restore(data);
            torcherino.setChanged();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean boolean_1) {
        TorcherinoLogic.neighborUpdate(state, level, pos, neighborBlock, orientation, boolean_1, (be) -> {
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
