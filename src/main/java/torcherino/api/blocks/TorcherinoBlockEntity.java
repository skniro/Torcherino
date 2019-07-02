package torcherino.api.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TorcherinoBlockEntity extends BlockEntity
{
    public TorcherinoBlockEntity() { super(Registry.BLOCK_ENTITY.get(new Identifier("torcherino", "torcherino"))); }
}
