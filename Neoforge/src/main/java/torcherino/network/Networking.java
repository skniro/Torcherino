package torcherino.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import torcherino.Torcherino;

@EventBusSubscriber(modid = Torcherino.MOD_ID)
public class Networking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Torcherino.MOD_ID);
        registrar.playBidirectional(
                OpenScreenMessage.TYPE,
                OpenScreenMessage.CODEC,
                ServerPayloadHandler::handleData,
                ClientPayloadHandler::handleData
        );

        registrar.playBidirectional(
                S2CTierSyncMessage.TYPE,
                S2CTierSyncMessage.CODEC,
                ServerPayloadHandler::handleTier,
                ClientPayloadHandler::handleTier
        );

        registrar.playBidirectional(
                ValueUpdateMessage.TYPE,
                ValueUpdateMessage.CODEC,
                ServerPayloadHandler::handleValue,
                ClientPayloadHandler::handleValue
        );
    }

}
