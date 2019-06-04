package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import torcherino.block.ModBlocks;
import torcherino.networking.PacketConsumers;

@EnvironmentInterface(itf = ClientModInitializer.class, value = EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer
{


	@Override public void onInitialize()
	{
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		ModBlocks.onInitialize();
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}
}
