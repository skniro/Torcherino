package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import torcherino.api.TorcherinoBlacklistAPI;
import torcherino.api.TorcherinoBlacklistInitializer;
import torcherino.block.ModBlocks;
import torcherino.networking.PacketConsumers;

@EnvironmentInterface(itf = ClientModInitializer.class, value = EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer
{
	@Override public void onInitialize()
	{
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		ModBlocks.onInitialize();
		// Blacklist computer craft turtles (known to crash otherwise)
		if (FabricLoader.getInstance().isModLoaded("computercraft"))
		{
			TorcherinoBlacklistAPI.INSTANCE.blacklistIdentifier(new Identifier("computercraft", "turtle_normal"));
			TorcherinoBlacklistAPI.INSTANCE.blacklistIdentifier(new Identifier("computercraft", "turtle_advanced"));
		}
		FabricLoader.getInstance().getEntrypoints("torcherino", TorcherinoBlacklistInitializer.class).forEach(TorcherinoBlacklistInitializer::onTorcherinoBlacklist);
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}
}
