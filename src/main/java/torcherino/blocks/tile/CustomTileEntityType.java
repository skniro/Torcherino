package torcherino.blocks.tile;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomTileEntityType<T extends TileEntity> extends TileEntityType<T>
{
    private final Predicate<Block> predicate;

    public CustomTileEntityType(Supplier<? extends T> factory, Predicate<Block> isValidBlock, Type<?> dataFixerType)
    {
        super(factory, null, dataFixerType);
        predicate = isValidBlock;
    }

    @Override
    public boolean isValidBlock(Block block)
    {
        return predicate.test(block);
    }
}
