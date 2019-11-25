package torcherino.api.blocks.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import torcherino.api.blocks.JackoLanterinoBlock;
import torcherino.api.blocks.LanterinoBlock;
import torcherino.api.blocks.TorcherinoBlock;
import torcherino.api.blocks.WallTorcherinoBlock;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity>
{
    public TocherinoBlockEntityType(Supplier<TorcherinoBlockEntity> supplier, Type type) { super(supplier, null, type); }

    @Override
    public boolean supports(Block block)
    {
        return TorcherinoBlock.class.equals(block.getClass()) || WallTorcherinoBlock.class.equals(block.getClass()) ||
                JackoLanterinoBlock.class.equals(block.getClass()) || LanterinoBlock.class.equals(block.getClass());
    }
}
