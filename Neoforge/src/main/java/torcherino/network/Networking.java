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
        registrar.playBidirectional(
                OpenScreenMessage.TYPE,
                OpenScreenMessage.CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,
                        ServerPayloadHandler::handleData
                )
        );

        registrar.playBidirectional(
                S2CTierSyncMessage.TYPE,
                S2CTierSyncMessage.CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleTier,
                        ServerPayloadHandler::handleTier
                )
        );

        registrar.playBidirectional(
                ValueUpdateMessage.TYPE,
                ValueUpdateMessage.CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleValue,
                        ServerPayloadHandler::handleValue
                )
        );
    }

}
