package torcherino.blocks;

import net.minecraft.block.BlockTorchWall;
import net.minecraft.init.Blocks;

public class TorcherinoWallBlock extends BlockTorchWall
{
	protected TorcherinoWallBlock()
	{
		super(Properties.from(Blocks.WALL_TORCH));
	}
}
