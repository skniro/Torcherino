package torcherino.network;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.entity.TorcherinoBlockEntity;

public class ServerPayloadHandler {
    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public static void handleData(final OpenScreenMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
        });
    }

    public static void handleTier(final S2CTierSyncMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        ImmutableMap<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        context.enqueueWork(() -> (
                new S2CTierSyncMessage(tiers))
        );
    }

    @SuppressWarnings("ConstantConditions")
    public static void handleValue(final ValueUpdateMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        context.enqueueWork(() -> {
            if (context.player().level().getBlockEntity(message.pos()) instanceof TorcherinoBlockEntity blockEntity) {
                if (!blockEntity.readClientData(message.xRange(), message.zRange(), message.yRange(), message.speed(), message.redstoneMode())) {
                    Torcherino.LOGGER.error("Data received from " + context.player().getName().getString() + "(" + context.player().getStringUUID() + ") is invalid.");
                }
            }
        });
    }
}
