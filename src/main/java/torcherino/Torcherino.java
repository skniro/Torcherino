package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import torcherino.block.ModBlocks;
import torcherino.networking.PacketConsumers;

@EnvironmentInterface(itf = ClientModInitializer.class, value = EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer
{
	@Override public void onInitialize()
	{
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		ModBlocks.onInitialize();
		if (FabricLoader.getInstance().isModLoaded("computercraft"))
		{
			Utils.blacklistString("computercraft:turtle_normal");
			Utils.blacklistString("computercraft:turtle_advanced");
		}
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}
}
