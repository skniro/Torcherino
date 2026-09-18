package torcherino.block;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import torcherino.api.TierSupplier;
import torcherino.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WeatheringLanterinoBlock extends CopperLanterinoBlock implements WeatheringCopper, EntityBlock, TierSupplier {
    private final WeatheringCopper.WeatherState weatherState;

    public WeatheringLanterinoBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, Identifier tier) {
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

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}
