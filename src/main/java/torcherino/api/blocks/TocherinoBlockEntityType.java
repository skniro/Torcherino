package torcherino.api.blocks;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Supplier;

public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity>
{
    public TocherinoBlockEntityType(Supplier<TorcherinoBlockEntity> supplier_1, Set<Block> set_1, Type type_1)
    {
        super(supplier_1, set_1, type_1);
    }

    @Override
    public boolean supports(Block block_1)
    {
        return TorcherinoBlock.class.equals(block_1.getClass()) || WallTorcherinoBlock.class.equals(block_1.getClass()) ||
                LanterinoBlock.class.equals(block_1.getClass());
    }
}
