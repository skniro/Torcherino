package torcherino.temp;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

// todo: replace with fabric networking hook
@FunctionalInterface
public interface PlayerDisconnectCallback {
    Event<PlayerDisconnectCallback> EVENT = EventFactory.createArrayBacked(PlayerDisconnectCallback.class, listeners ->
            (player) -> Arrays.stream(listeners).forEach(listener -> listener.onPlayerDisconnected(player)));

    void onPlayerDisconnected(ServerPlayer player);
}
