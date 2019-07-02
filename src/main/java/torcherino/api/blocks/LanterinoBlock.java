package torcherino.api.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public class LanterinoBlock extends Block
{
    public LanterinoBlock(Identifier tierID) { super(FabricBlockSettings.copy(Blocks.CARVED_PUMPKIN).build()); }
}
