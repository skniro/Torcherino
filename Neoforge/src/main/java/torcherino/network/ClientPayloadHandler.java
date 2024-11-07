package torcherino.network;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import torcherino.Torcherino;
import torcherino.TorcherinoImpl;
import torcherino.api.TorcherinoAPI;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;


public class ClientPayloadHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }


    public static void handleData(final OpenScreenMessage data, final IPayloadContext context) {
      OpenScreenMessage.openTorcherinoScreen(data, context);
    }


    public static void handleTier(final S2CTierSyncMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        context.enqueueWork(() -> (
                (TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(message.tiers()));
    }

    @SuppressWarnings("ConstantConditions")
    public static void handleValue(final ValueUpdateMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        Level level = context.player().level();
        context.enqueueWork(() -> {
            if (level.getBlockEntity(message.pos()) instanceof TorcherinoBlockEntity blockEntity) {
                if(!blockEntity.readClientData(message.xRange(), message.zRange(), message.yRange(), message.speed(), message.redstoneMode())) {
                    Torcherino.LOGGER.error("Data received from " + context.player().getName().getString() + "(" + context.player().getStringUUID() + ") is invalid.");
                }
            }
        });
    }

}
