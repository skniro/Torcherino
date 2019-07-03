package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Identifier;

public class WallTorcherinoBlock extends WallTorchBlock
{
    public WallTorcherinoBlock(Identifier tierID) { super(FabricBlockSettings.copy(Blocks.WALL_TORCH).build()); }
}
