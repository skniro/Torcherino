package torcherino.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import torcherino.Torcherino;
import torcherino.TorcherinoImpl;
import torcherino.api.TorcherinoAPI;
import torcherino.block.entity.TorcherinoBlockEntity;


public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }


    public static void handleData(final OpenScreenMessage data, final IPayloadContext context) {
      OpenScreenMessage.openTorcherinoScreen(data, context);
    }


    public static void handleTier(final S2CTierSyncMessage message, IPayloadContext contextSupplier) {
        ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(message.tiers());
    }

    @SuppressWarnings("ConstantConditions")
    public static void handleValue(final ValueUpdateMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        if (context.player().level().getBlockEntity(message.pos()) instanceof TorcherinoBlockEntity blockEntity) {
            if (!blockEntity.readClientData(message.xRange(), message.zRange(), message.yRange(), message.speed(), message.redstoneMode())) {
                Torcherino.LOGGER.error("Data received from " + context.player().getName().getString() + "(" + context.player().getStringUUID() + ") is invalid.");
            }
        }
    }

}
