package torcherino.block;

import com.google.common.collect.BiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import torcherino.api.TierSupplier;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.Optional;

public class WeatheringLanterinoBlock extends CopperLanterinoBlock implements WeatheringCopper, EntityBlock, TierSupplier {
    private final WeatherState weatherState;

    public WeatheringLanterinoBlock(WeatherState weatherState, Properties properties, ResourceLocation tier) {
        super(properties, tier);
        this.weatherState = weatherState;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        Optional<Block> nextOpt = WeatheringCopper.getNext(state.getBlock());
        TorcherinoBlockEntity.Data saved = null;
        if (be instanceof TorcherinoBlockEntity oldBE) {
            saved = TorcherinoBlockEntity.Data.from(oldBE);
        }

        BlockState newState = nextOpt.get().defaultBlockState();

        level.setBlock(pos, newState, Block.UPDATE_ALL);

        if (saved != null) {
            BlockEntity newBE = level.getBlockEntity(pos);
            if (newBE instanceof TorcherinoBlockEntity torcherinoBE) {
                torcherinoBE.restore(saved);
            }
        }
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        boolean handled = false;

        if (item == Items.HONEYCOMB) {
            if (!level.isClientSide()) {
                BiMap<Block, Block> waxables = (BiMap<Block, Block>) HoneycombItem.WAXABLES.get();
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
                BiMap<Block, Block> waxOffMap = (BiMap<Block, Block>) HoneycombItem.WAX_OFF_BY_BLOCK.get();
                Block unwaxed = waxOffMap.get(state.getBlock());
                if (unwaxed != null) {
                    swapPreserveTorcherinoBE((ServerLevel) level, pos, state, unwaxed.defaultBlockState());
                    stack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> {
                    });
                    level.levelEvent(player, 3005, pos, 0);
                    handled = true;
                }

                BiMap<Block, Block> prevMap = (BiMap<Block, Block>) WeatheringCopper.PREVIOUS_BY_BLOCK.get();
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

    protected boolean isRandomlyTicking(BlockState blockState) {
        return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
    }

    public WeatherState getAge() {
        return this.weatherState;
    }
}
