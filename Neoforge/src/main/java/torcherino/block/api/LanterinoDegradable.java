package torcherino.block.api;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import torcherino.block.WeatheringLanterinoBlock;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public interface LanterinoDegradable extends ChangeOverTimeBlock<LanterinoDegradable.DegradationLevel> {

    static Optional<Block> getPrevious(Block pBlock) {
        if (pBlock instanceof WeatheringLanterinoBlock weatheringLanterinoBlock) {
            ResourceLocation tierID = weatheringLanterinoBlock.getTier();
            return Optional.ofNullable(LanterinoOxidizableRegistry.getPreviousByBlock(tierID).get(pBlock));
        }
        return Optional.empty();
    }

    static Block getFirst(Block pBlock) {
        Block block = pBlock;
        if (block instanceof WeatheringLanterinoBlock weatheringLanterinoBlock) {
            ResourceLocation tierID = weatheringLanterinoBlock.getTier();
            for (Block block1 = LanterinoOxidizableRegistry.getPreviousByBlock(tierID).get(pBlock); block1 != null; block1 = LanterinoOxidizableRegistry.getPreviousByBlock(tierID).get(block1)) {
                block = block1;
            }
        }
        return block;
    }

    static Optional<BlockState> getPrevious(BlockState pState) {
        return getPrevious(pState.getBlock()).map((p_154903_) -> {
            return p_154903_.withPropertiesOf(pState);
        });
    }

    static Optional<Block> getNext(Block pBlock, ResourceLocation tierID) {
        return Optional.ofNullable(LanterinoOxidizableRegistry.getNextByBlock(tierID).get(pBlock));
    }

    static BlockState getFirst(BlockState pState) {
        return getFirst(pState.getBlock()).withPropertiesOf(pState);
    }

    default Optional<BlockState> getNext(BlockState pState) {
        if (pState.getBlock() instanceof WeatheringLanterinoBlock weatheringLanterinoBlock) {
            ResourceLocation tierID = weatheringLanterinoBlock.getTier();
            return getNext(pState.getBlock(), tierID).map((p_154896_) -> {
                return p_154896_.withPropertiesOf(pState);
            });
        }
        return Optional.empty();
    }

    default float getChanceModifier() {
        return this.getAge() == DegradationLevel.UNAFFECTED ? 0.75F : 1.0F;
    }

    /*public static enum DegradationLevel implements StringRepresentable {
        UNAFFECTED("unaffected"),
        EXPOSED("exposed"),
        WEATHERED("weathered"),
        OXIDIZED("oxidized");

        public static final IntFunction<DegradationLevel> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        public static final Codec<DegradationLevel> CODEC = StringRepresentable.fromEnum(DegradationLevel::values);
        public static final StreamCodec<ByteBuf, DegradationLevel> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
        private final String name;

        private DegradationLevel(String string) {
            this.name = string;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public DegradationLevel next() {
            return (DegradationLevel)BY_ID.apply(this.ordinal() + 1);
        }

        public DegradationLevel previous() {
            return (DegradationLevel)BY_ID.apply(this.ordinal() - 1);
        }*/

    public static enum DegradationLevel {
        UNAFFECTED,
        EXPOSED,
        WEATHERED,
        OXIDIZED;
    }
}
