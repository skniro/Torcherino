package torcherino.blocks.miscellaneous;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.ITextComponent;
import torcherino.network.Networker;

public class TorcherinoContainer extends Container
{
	// This class is simply for wrapping the tileEntity and storing variables
	private int speed, xRange, zRange, yRange, redstoneMode;
	private final ITextComponent displayName;
	private final TorcherinoTileEntity tileEntity;

	public TorcherinoContainer(TorcherinoTileEntity tileEntity, ITextComponent displayName)
	{
		this.tileEntity = tileEntity;
		this.displayName = displayName;
	}

	@Override public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}

	public void setXRange(int xRange)
	{
		this.xRange = xRange;
	}

	public void setZRange(int zRange)
	{
		this.zRange = zRange;
	}

	public void setYRange(int yRange)
	{
		this.yRange = yRange;
	}

	public void setRedstoneMode(int redstoneMode)
	{
		this.redstoneMode = redstoneMode;
	}

	public int getXRange(){ return xRange; }

	public int getZRange(){ return zRange; }

	public int getYRange(){ return yRange; }

	public int getMaxXZRange(){ return tileEntity.getTier().XZ_RANGE; }

	public int getMaxYRange(){ return tileEntity.getTier().Y_RANGE; }

	public int getSpeed(){ return speed; }

	public int getMaxSpeed(){ return tileEntity.getTier().MAX_SPEED; }

	public int getRedstoneMode(){ return redstoneMode; }

	public ITextComponent getDisplayName(){ return displayName; }

	@Override public void onContainerClosed(EntityPlayer player)
	{
		if (player instanceof EntityPlayerSP)
		{
			Networker.INSTANCE.torcherinoChannel.sendToServer(new Networker.ValueUpdateMessage(tileEntity.getPos(), getXRange(), getZRange(), getYRange(), getSpeed(), getRedstoneMode()));
		}
		super.onContainerClosed(player);
	}
}
