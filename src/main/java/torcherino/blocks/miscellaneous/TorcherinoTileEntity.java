package torcherino.blocks.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import torcherino.Utilities;
import torcherino.blocks.ModBlocks;
import javax.annotation.Nullable;

public class TorcherinoTileEntity extends TileEntity implements IInteractionObject
{
	private ITextComponent customname;

	public TorcherinoTileEntity(){ super(ModBlocks.INSTANCE.TORCHERINO_TILE_ENTITY_TYPE); }

	@Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new TorcherinoContainer(this);
	}

	@Override public String getGuiID()
	{
		return Utilities.resloc("torcherino").toString();
	}

	@Override public ITextComponent getName()
	{
		return hasCustomName() ? customname : new TextComponentString("Default");
	}

	@Override public boolean hasCustomName()
	{
		return customname == null;
	}

	@Nullable @Override public ITextComponent getCustomName()
	{
		return customname;
	}
}
