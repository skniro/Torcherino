package torcherino.blocks.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class TorcherinoContainer extends Container
{
	private final TorcherinoTileEntity tileEntity;

	public TorcherinoContainer(TorcherinoTileEntity tileTorcherino)
	{
		this.tileEntity = tileTorcherino;
	}

	@Override public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
}
