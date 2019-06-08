package torcherino.blocks.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class TorcherinoContainer extends Container
{
	private final TorcherinoTileEntity tileEntity;

	public TorcherinoContainer(TorcherinoTileEntity tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	@Override public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	public TorcherinoTileEntity getTileEntity()
	{
		return this.tileEntity;
	}
}
