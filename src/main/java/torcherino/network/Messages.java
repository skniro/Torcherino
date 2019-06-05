package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.Utils;
import java.util.function.Supplier;

public class Messages
{
	public static class KeystateUpdate
	{
		private final boolean pressed;
		private final PacketBuffer buf;

		KeystateUpdate(boolean pressed, PacketBuffer buf)
		{
			this.pressed = pressed;
			this.buf = buf;
		}

		public static void encode(KeystateUpdate msg, PacketBuffer buf){ buf.writeBoolean(msg.pressed); }

		public static KeystateUpdate decode(PacketBuffer buf){ return new KeystateUpdate(buf.readBoolean(), buf); }

		public static void handle(KeystateUpdate msg, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> Utils.keyStates.put(ctx.get().getSender(), msg.pressed));
			ctx.get().setPacketHandled(true);
		}
	}
}
