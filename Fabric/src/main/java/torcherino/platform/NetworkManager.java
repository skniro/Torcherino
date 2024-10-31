package torcherino.platform;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.Collections;

public class NetworkManager {
	public static void sendToAll(CustomPacketPayload payload, MinecraftServer server) {
		send(payload, PlayerLookup.all(server));
	}

	public static void sendToPlayer(CustomPacketPayload payload, ServerPlayer serverPlayerEntity) {
		send(payload, Collections.singletonList(serverPlayerEntity));
	}

	public static void sendToWorld(CustomPacketPayload payload, ServerLevel world) {
		send(payload, PlayerLookup.world(world));
	}


	public static void sendToTracking(CustomPacketPayload payload, BlockEntity blockEntity) {
		send(payload, PlayerLookup.tracking(blockEntity));
	}

	public static void send(CustomPacketPayload payload, Collection<ServerPlayer> players) {
		for (ServerPlayer player : players) {
			ServerPlayNetworking.send(player, payload);
		}
	}
}
