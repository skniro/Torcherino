package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.Utilities;
import java.util.function.Supplier;

public class Networker
{

	public static final Networker INSTANCE = new Networker();

	public SimpleChannel torcherinoChannel;

	public void initialise()
	{
		torcherinoChannel = NetworkRegistry.newSimpleChannel(Utilities.resloc("channel"), () -> "1", version ->
		{return version.equals("1");}, version ->
		{return version.equals("1");});
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
		private final PacketBuffer buf;


		public ValueUpdateMessage(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode, PacketBuffer buf)
		{
			this.pos = pos;
			this.xRange = xRange;
			this.zRange = zRange;
			this.yRange = yRange;
			this.speed = speed;
			this.redstoneMode = redstoneMode;
			this.buf = buf;
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
			return new ValueUpdateMessage(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf);
		}

		public static void handle(ValueUpdateMessage msg, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				Utilities.LOGGER.info("Received packet. Values are: ");
				Utilities.LOGGER.info("X Range: " + msg.xRange);
				Utilities.LOGGER.info("Z Range: " + msg.zRange);
				Utilities.LOGGER.info("Y Range: " + msg.yRange);
				Utilities.LOGGER.info("Speed: " + msg.speed);
				Utilities.LOGGER.info("Redstone Mode: " + msg.redstoneMode);
					});
			ctx.get().setPacketHandled(true);
		}
	}
}
