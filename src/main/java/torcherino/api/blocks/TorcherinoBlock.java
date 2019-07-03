package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.Identifier;

public class TorcherinoBlock extends TorchBlock
{
    public TorcherinoBlock(Identifier tierID) { super(FabricBlockSettings.copy(Blocks.TORCH).build()); }
}
