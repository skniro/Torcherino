package torcherino.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import static torcherino.Torcherino.TORCHERINO_TILE_ENTITY_TYPE;
import torcherino.Utils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TorcherinoTileEntity extends TileEntity implements ITickable, IInteractionObject
{
	private static final String[] MODES = new String[]{"chat.torcherino.hint.area.stopped", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n", "chat.torcherino.hint.area.n"};
	private boolean poweredByRedstone;
	private int randomTicks = 3, speed, MAX_SPEED;
	private byte cachedMode, mode;
	private int xMin, yMin, zMin;
	private int xMax, yMax, zMax;
	private ITextComponent customName;

	public TorcherinoTileEntity(){ super(TORCHERINO_TILE_ENTITY_TYPE); }

	public TorcherinoTileEntity(int speed)
	{
		this();
		MAX_SPEED = speed;
	}

	public void setPoweredByRedstone(boolean powered){ poweredByRedstone = powered; }

	@Override public SPacketUpdateTileEntity getUpdatePacket(){ return new SPacketUpdateTileEntity(getPos(), 126, write(new NBTTagCompound())); }

	@Override public void tick()
	{
		if (world.isRemote || poweredByRedstone || mode == 0 || speed == 0) return;
		if (cachedMode != mode)
		{
			xMin = pos.getX() - mode;
			xMax = pos.getX() + mode;
			yMin = pos.getY() - 1;
			yMax = pos.getY() + 1;
			zMin = pos.getZ() - mode;
			zMax = pos.getZ() + mode;
			cachedMode = mode;
		}
		randomTicks = world.getGameRules().getInt("randomTickSpeed");
		for (int x = xMin; x <= xMax; x++)
			for (int y = yMin; y <= yMax; y++)
				for (int z = zMin; z <= zMax; z++)
					tickBlock(new BlockPos(x, y, z));
	}

	private void tickBlock(BlockPos pos)
	{
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (Utils.isBlockBlacklisted(block)) return;
		if (block.getTickRandomly(blockState) && world.getRandom().nextInt(MathHelper.clamp(4096 / (speed * Utils.randomTickSpeedRate), 1, 4096)) < randomTicks) block.randomTick(blockState, world, pos, world.getRandom());
		if (!block.hasTileEntity(blockState)) return;
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || tileEntity.isRemoved() || Utils.isTileEntityBlacklisted(tileEntity) || !(tileEntity instanceof ITickable)) return;
		for (int i = 0; i < speed; i++)
		{
			if (tileEntity.isRemoved()) break;
			((ITickable) tileEntity).tick();
		}
	}

	public void changeMode(boolean modifier)
	{
		if (modifier) if (speed < MAX_SPEED) speed += MAX_SPEED / 4;
		else speed = 0;
		else if (mode < MODES.length - 1) mode++;
		else mode = 0;
	}

	public TextComponentTranslation getDescription()
	{
		return new TextComponentTranslation("chat.torcherino.hint.layout", new TextComponentTranslation(MODES[mode], 2 * mode + 1), new TextComponentTranslation("chat.torcherino.hint.speed", speed * 100));
	}

	@Override @Nonnull public NBTTagCompound write(@Nonnull NBTTagCompound tag)
	{
		super.write(tag);
		tag.setInt("Speed", speed);
		tag.setInt("MaxSpeed", MAX_SPEED);
		tag.setByte("Mode", mode);
		tag.setBoolean("PoweredByRedstone", poweredByRedstone);
		return tag;
	}

	@Override public void read(@Nonnull NBTTagCompound tag)
	{
		super.read(tag);
		speed = tag.getInt("Speed");
		MAX_SPEED = tag.getInt("MaxSpeed");
		mode = tag.getByte("Mode");
		poweredByRedstone = tag.getBoolean("PoweredByRedstone");
	}

	@Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new TorcherinoContainer(this);
	}

	@Override public String getGuiID()
	{
		return Utils.getId("torcherino_gui").toString();
	}

	@Override public ITextComponent getName()
	{
		return hasCustomName() ? getCustomName() : world.getBlockState(pos).getBlock().getNameTextComponent();
	}

	@Override public boolean hasCustomName()
	{
		return customName != null;
	}

	@Nullable @Override public ITextComponent getCustomName()
	{
		return customName;
	}

	public void setCustomName(ITextComponent name)
	{
		customName = name;
	}
}
