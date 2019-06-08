package torcherino.blocks.miscellaneous;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import torcherino.TorcherinoTiers;
import torcherino.Utilities;
import torcherino.blocks.ModBlocks;
import javax.annotation.Nullable;

public class TorcherinoTileEntity extends TileEntity implements IInteractionObject
{
	private ITextComponent customName;
	private int xRange, yRange, zRange, speed;
	private TorcherinoTiers.Tier tier;

	public TorcherinoTileEntity(TorcherinoTiers.Tier tier)
	{
		super(ModBlocks.INSTANCE.TORCHERINO_TILE_ENTITY_TYPE);
		this.tier = tier;
	}

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
		return hasCustomName() ? customName : new TextComponentTranslation(world.getBlockState(pos).getBlock().getTranslationKey());
	}

	@Override public boolean hasCustomName()
	{
		return customName != null;
	}

	@Nullable @Override public ITextComponent getCustomName()
	{
		return customName;
	}

	public void setCustomName(@Nullable ITextComponent customName)
	{
		this.customName = customName;
	}

	public TorcherinoTiers.Tier getTier(){ return tier; }

	public int getxRange(){ return xRange; }

	public int getyRange(){ return yRange; }

	public int getzRange(){ return zRange; }

	public int getSpeed(){ return speed; }

	public void read(NBTTagCompound compound)
	{
		super.read(compound);
		if (compound.contains("CustomName", 8))
		{
			setCustomName(ITextComponent.Serializer.fromJson(compound.getString("CustomName")));
		}
	}

	public NBTTagCompound write(NBTTagCompound compound)
	{
		super.write(compound);
		if (hasCustomName())
		{
			compound.setString("CustomName", ITextComponent.Serializer.toJson(getCustomName()));
		}
		return compound;
	}

	@Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		if (net.getDirection() == EnumPacketDirection.CLIENTBOUND)
		{
			NBTTagCompound tag = pkt.getNbtCompound();
			if (tag.contains("CustomName", 8))
			{
				setCustomName(ITextComponent.Serializer.fromJson(tag.getString("CustomName")));
			}
		}
	}

	@Nullable public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 127, this.getUpdateTag());
	}


	@Override public NBTTagCompound getUpdateTag()
	{
		return write(new NBTTagCompound());
	}
}
