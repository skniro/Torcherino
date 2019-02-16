package torcherino.Blocks;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTorcherino extends BlockTorch
{
	public BlockTorcherino()
	{
		super(Properties.create(Material.CIRCUITS).doesNotBlockMovement().hardnessAndResistance(0).lightValue(14).sound(SoundType.WOOD));
	}
}
