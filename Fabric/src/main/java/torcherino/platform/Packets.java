package torcherino.platform;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import torcherino.platform.payload.OpenTorchrinoScreenPayload;
import torcherino.platform.payload.TorchrinoTierPayload;
import torcherino.platform.payload.UpdateTorchrinoPayload;


public class Packets {
	public static void register() {
		clientbound(PayloadTypeRegistry.clientboundPlay());
		serverbound(PayloadTypeRegistry.serverboundPlay());
	}

	private static void clientbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(TorchrinoTierPayload.TYPE, TorchrinoTierPayload.CODEC);
        registry.register(OpenTorchrinoScreenPayload.TYPE, OpenTorchrinoScreenPayload.CODEC);
	}

	private static void serverbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(UpdateTorchrinoPayload.TYPE, UpdateTorchrinoPayload.CODEC);
	}

}
