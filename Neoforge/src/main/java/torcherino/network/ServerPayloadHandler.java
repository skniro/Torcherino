package torcherino.network;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
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
/*            Minecraft minecraft = Minecraft.getInstance();
            minecraft.submitAsync(() -> {
                if (minecraft.player.level().getBlockEntity(data.pos()) instanceof TorcherinoBlockEntity blockEntity) {
                    TorcherinoScreen screen = new TorcherinoScreen(Component.translatable(data.title()), data.xRange(), data.zRange(), data.yRange(),
                            data.speed(), data.redstoneMode(), blockEntity.getBlockPos(), blockEntity.getTier());
                    minecraft.setScreen(screen);
                }
            });*/
        });
    }

    public static void handleTier(final S2CTierSyncMessage message, IPayloadContext contextSupplier) {
        IPayloadContext context = contextSupplier;
        ImmutableMap<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
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
