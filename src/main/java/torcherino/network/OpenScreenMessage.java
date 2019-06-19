package torcherino.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import torcherino.client.gui.TorcherinoScreen;
import java.util.function.Supplier;
public class OpenScreenMessage
{
	private final BlockPos pos;
	private final ITextComponent title;
	private final int xRange;
	private final int zRange;
	private final int yRange;
	private final int speed;
	private final int redstoneMode;

	public OpenScreenMessage(BlockPos pos, ITextComponent title, int xRange, int zRange, int yRange, int speed, int redstoneMode)
	{
		this.pos = pos;
		this.title = title;
		this.xRange = xRange;
		this.zRange = zRange;
		this.yRange = yRange;
		this.speed = speed;
		this.redstoneMode = redstoneMode;
	}

	static void encode(OpenScreenMessage msg, PacketBuffer buf)
	{
		buf.writeBlockPos(msg.pos);
		buf.writeTextComponent(msg.title);
		buf.writeInt(msg.xRange);
		buf.writeInt(msg.zRange);
		buf.writeInt(msg.yRange);
		buf.writeInt(msg.speed);
		buf.writeInt(msg.redstoneMode);
	}

	static OpenScreenMessage decode(PacketBuffer buf){ return new OpenScreenMessage(buf.readBlockPos(), buf.readTextComponent(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()); }

	static void handle(OpenScreenMessage msg, Supplier<NetworkEvent.Context> ctx)
	{
		NetworkEvent.Context context = ctx.get();
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.addScheduledTask(() -> {
			World world = minecraft.player.world;
			TileEntity tileEntity = world.getTileEntity(msg.pos);
			if (tileEntity instanceof TorcherinoTileEntity)
			{
				TorcherinoScreen screen = new TorcherinoScreen((TorcherinoTileEntity) tileEntity, msg.title, msg.xRange, msg.zRange, msg.yRange, msg.speed, msg.redstoneMode);
				Minecraft.getInstance().mouseHelper.ungrabMouse();
				Minecraft.getInstance().displayGuiScreen(screen);
			}
		});
		context.setPacketHandled(true);
	}
}