package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class TorcherinoWallBlock extends Block
{
    public TorcherinoWallBlock(Identifier tierID) { super(FabricBlockSettings.copy(Blocks.WALL_TORCH).build()); }
}
