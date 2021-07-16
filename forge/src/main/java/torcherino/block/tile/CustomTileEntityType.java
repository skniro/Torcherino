package torcherino.block.tile;

import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class CustomTileEntityType<T extends BlockEntity> extends BlockEntityType<T> {
    private final Predicate<Block> PREDICATE;

    @SuppressWarnings("ConstantConditions")
    public CustomTileEntityType(final Supplier<? extends T> factory, final Predicate<Block> isBlockValid, final Type<?> dataFixerType) {
        super(factory, null, dataFixerType);
        PREDICATE = isBlockValid;
    }

    @Override
    public boolean isValid(final Block block) {
        return PREDICATE.test(block);
    }
}
