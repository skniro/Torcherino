package torcherino.blocks.miscellaneous;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import torcherino.TorcherinoTiers;
import torcherino.Utilities;
import torcherino.blocks.LanterinoBlock;
import torcherino.blocks.ModBlocks;
import torcherino.blocks.TorcherinoBlock;
import torcherino.blocks.TorcherinoWallBlock;
import javax.annotation.Nullable;

public class TorcherinoTileEntity extends TileEntity implements IInteractionObject
{
	private ITextComponent customName;
	private int xRange, yRange, zRange, speed, redstoneMode;
	private TorcherinoTiers.Tier tier;

	public TorcherinoTileEntity()
	{
		super(ModBlocks.INSTANCE.TORCHERINO_TILE_ENTITY_TYPE);
	}

	@Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new TorcherinoContainer(this, getName());
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

	public TorcherinoTiers.Tier getTier()
	{
		if (tier == null)
		{
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof LanterinoBlock) tier = ((LanterinoBlock) block).getTier();
			else if (block instanceof TorcherinoBlock) tier = ((TorcherinoBlock) block).getTier();
			else if (block instanceof TorcherinoWallBlock) tier = ((TorcherinoWallBlock) block).getTier();
		}
		return tier;
	}

	public void read(NBTTagCompound compound)
	{
		super.read(compound);
		if (compound.contains("CustomName", 8))
		{
			setCustomName(ITextComponent.Serializer.fromJson(compound.getString("CustomName")));
		}
		this.xRange = compound.getInt("XRange");
		this.zRange = compound.getInt("ZRange");
		this.yRange = compound.getInt("YRange");
		this.speed = compound.getInt("Speed");
		this.redstoneMode = compound.getInt("RedstoneMode");
	}

	public NBTTagCompound write(NBTTagCompound compound)
	{
		super.write(compound);
		if (hasCustomName())
		{
			compound.setString("CustomName", ITextComponent.Serializer.toJson(getCustomName()));
		}
		compound.setInt("XRange", this.xRange);
		compound.setInt("ZRange", this.zRange);
		compound.setInt("YRange", this.yRange);
		compound.setInt("Speed", this.speed);
		compound.setInt("RedstoneMode", this.redstoneMode);
		return compound;
	}

	public void writeClientData(PacketBuffer packetBuffer)
	{
		packetBuffer.writeBlockPos(pos);
		packetBuffer.writeTextComponent(getName());
		packetBuffer.writeInt(this.xRange);
		packetBuffer.writeInt(this.zRange);
		packetBuffer.writeInt(this.yRange);
		packetBuffer.writeInt(this.speed);
		packetBuffer.writeInt(this.redstoneMode);
	}

	public void readClientData(int xRange, int zRange, int yRange, int speed, int redstoneMode)
	{
		this.xRange = xRange;
		this.zRange = zRange;
		this.yRange = yRange;
		this.speed = speed;
		this.redstoneMode = redstoneMode;
	}
}
