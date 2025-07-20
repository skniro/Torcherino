package torcherino.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import torcherino.Torcherino;

@EventBusSubscriber(modid = Torcherino.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Networking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Torcherino.MOD_ID);
        registrar.playToClient(
                OpenScreenMessage.TYPE,
                OpenScreenMessage.CODEC,
                ClientPayloadHandler::handleData
        );

        registrar.playToClient(
                S2CTierSyncMessage.TYPE,
                S2CTierSyncMessage.CODEC,
                ClientPayloadHandler::handleTier
        );

        registrar.playToServer(
                ValueUpdateMessage.TYPE,
                ValueUpdateMessage.CODEC,
                ServerPayloadHandler::handleValue
        );
    }
}
