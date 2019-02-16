package torcherino.Blocks;

import net.minecraft.block.BlockTorchWall;

public class BlockTorcherinoWall extends BlockTorchWall
{
	private BlockTorcherino BASE;
	public BlockTorcherinoWall(BlockTorcherino base)
	{
		super(Properties.from(base));
		this.BASE = base;
	}
}
