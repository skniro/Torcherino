package torcherino.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.Utilities;

public class Networker
{
	public static final Networker INSTANCE = new Networker();

	public SimpleChannel torcherinoChannel;

	public void initialise()
	{
		int id = 0;
		torcherinoChannel = NetworkRegistry.newSimpleChannel(Utilities.resloc("channel"), () -> "1", version -> version.equals("1"), version -> version.equals("1"));
		torcherinoChannel.registerMessage(id++, ValueUpdateMessage.class, ValueUpdateMessage::encode, ValueUpdateMessage::decode, ValueUpdateMessage::handle);
		torcherinoChannel.registerMessage(id++, OpenScreenMessage.class, OpenScreenMessage::encode, OpenScreenMessage::decode, OpenScreenMessage::handle);
	}
}
