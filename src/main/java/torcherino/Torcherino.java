package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import torcherino.api.TorcherinoBlacklistAPI;
import torcherino.api.TorcherinoBlacklistInitializer;
import torcherino.blocks.Blocks;
import torcherino.network.PacketConsumers;

@EnvironmentInterface(itf = ClientModInitializer.class, value = EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer, TorcherinoBlacklistInitializer
{
	@Override public void onInitialize()
	{
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		Blocks.onInitialize();
		FabricLoader.getInstance().getEntrypoints("torcherino", TorcherinoBlacklistInitializer.class).forEach(TorcherinoBlacklistInitializer::onTorcherinoBlacklist);
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}

	// If your a mod developer this should be in a separate class to avoid a hard dependency on torcherino.
	@Override public void onTorcherinoBlacklist()
	{
		if (FabricLoader.getInstance().isModLoaded("computercraft"))
		{
			TorcherinoBlacklistAPI.INSTANCE.blacklistBlockEntity(new Identifier("computercraft", "turtle_normal"));
			TorcherinoBlacklistAPI.INSTANCE.blacklistBlockEntity(new Identifier("computercraft", "turtle_advanced"));
		}
	}
}
