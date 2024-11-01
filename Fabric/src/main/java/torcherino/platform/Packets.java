package torcherino.platform;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import torcherino.platform.payload.OpenTorchrinoScreenPayload;
import torcherino.platform.payload.TorchrinoTierPayload;
import torcherino.platform.payload.UpdateTorchrinoPayload;


public class Packets {
	public static void register() {
		clientbound(PayloadTypeRegistry.playS2C());
		serverbound(PayloadTypeRegistry.playC2S());
	}

	private static void clientbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(TorchrinoTierPayload.TYPE, TorchrinoTierPayload.CODEC);
        registry.register(UpdateTorchrinoPayload.TYPE, UpdateTorchrinoPayload.CODEC);
        registry.register(OpenTorchrinoScreenPayload.TYPE, OpenTorchrinoScreenPayload.CODEC);
	}

	private static void serverbound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(TorchrinoTierPayload.TYPE, TorchrinoTierPayload.CODEC);
        registry.register(UpdateTorchrinoPayload.TYPE, UpdateTorchrinoPayload.CODEC);
        registry.register(OpenTorchrinoScreenPayload.TYPE, OpenTorchrinoScreenPayload.CODEC);
	}

}
