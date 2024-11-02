package torcherino.network;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import torcherino.Torcherino;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Networking {
    /*@SubscribeEvent
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
    }*/

}
