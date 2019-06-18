package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.TorcherinoTiers;
import torcherino.Utilities;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import java.util.function.Supplier;

/*

	Now that this is implemented maybe I should make a new message for opening the screen and drop the gui factory extension point + container

 */


public class Networker
{

	public static final Networker INSTANCE = new Networker();

	public SimpleChannel torcherinoChannel;

	public void initialise()
	{
		torcherinoChannel = NetworkRegistry.newSimpleChannel(Utilities.resloc("channel"), () -> "1", version -> version.equals("1"), version -> version.equals("1"));
		torcherinoChannel.registerMessage(0, ValueUpdateMessage.class, ValueUpdateMessage::encode, ValueUpdateMessage::decode, ValueUpdateMessage::handle);
	}

	public static class ValueUpdateMessage
	{
		private final BlockPos pos;
		private final int xRange;
		private final int zRange;
		private final int yRange;
		private final int speed;
		private final int redstoneMode;


		public ValueUpdateMessage(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode)
		{
			this.pos = pos;
			this.xRange = xRange;
			this.zRange = zRange;
			this.yRange = yRange;
			this.speed = speed;
			this.redstoneMode = redstoneMode;
		}

		public static void encode(ValueUpdateMessage msg, PacketBuffer buf)
		{
			buf.writeBlockPos(msg.pos);
			buf.writeInt(msg.xRange);
			buf.writeInt(msg.zRange);
			buf.writeInt(msg.yRange);
			buf.writeInt(msg.speed);
			buf.writeInt(msg.redstoneMode);
		}

		public static ValueUpdateMessage decode(PacketBuffer buf)
		{
			return new ValueUpdateMessage(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
		}

		public static void handle(ValueUpdateMessage msg, Supplier<NetworkEvent.Context> ctx)
		{
			NetworkEvent.Context context = ctx.get();
			context.enqueueWork(() ->
			{
				World world = context.getSender().world;
				TileEntity tileEntity = world.getTileEntity(msg.pos);
				if (tileEntity instanceof TorcherinoTileEntity)
				{
					TorcherinoTileEntity torcherinoTileEntity = (TorcherinoTileEntity) tileEntity;
					TorcherinoTiers.Tier tier = torcherinoTileEntity.getTier();
					if (msg.xRange > tier.XZ_RANGE || msg.zRange > tier.XZ_RANGE || msg.yRange > tier.Y_RANGE || msg.speed > tier.MAX_SPEED || msg.redstoneMode > 3 || msg.xRange < 0 || msg.zRange < 0 || msg.yRange < 0 || msg.speed < 0 || msg.redstoneMode < 0)
						return;
					torcherinoTileEntity.readClientData(msg.xRange, msg.zRange, msg.yRange, msg.speed, msg.redstoneMode);
				}
			});
			context.setPacketHandled(true);
		}
	}
}
