package torcherino.api.blocks.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import torcherino.api.TierSupplier;

import java.util.function.Supplier;

@SuppressWarnings("SpellCheckingInspection")
public class TocherinoBlockEntityType extends BlockEntityType<TorcherinoBlockEntity>
{
    public TocherinoBlockEntityType(Supplier<TorcherinoBlockEntity> supplier, Type type) { super(supplier, null, type); }

    @Override
    public boolean supports(Block block) { return block instanceof TierSupplier; }
}
