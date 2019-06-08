package torcherino.blocks;

import net.minecraft.block.BlockTorch;
import net.minecraft.init.Blocks;

public class TorcherinoBlock extends BlockTorch
{
	protected TorcherinoBlock()
	{
		super(Properties.from(Blocks.TORCH));
	}
}
