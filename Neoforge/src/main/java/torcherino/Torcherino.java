package torcherino;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.platform.NetworkUtilsImpl;

@Mod(Torcherino.MOD_ID)
public final class Torcherino {
    public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
    public static final String MOD_ID = "torcherino";

    public static ResourceLocation resloc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, path);
    }

    public Torcherino(IEventBus eventBus) {
        Config.initialize();
        ModContent.initialise(eventBus);
        NetworkUtilsImpl.getInstance().initialize();
        eventBus.addListener(this::processIMC);
    }

    public static ResourceLocation getRl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void processIMC(InterModProcessEvent event) {
        event.getIMCStream().forEach((message) ->
        {
            String method = message.getMethod();
            Object value = message.getMessageSupplier().get();
            if (method.equals("blacklist_block")) {
                if (value instanceof ResourceLocation) {
                    TorcherinoAPI.INSTANCE.blacklistBlock((ResourceLocation) value);
                } else if (value instanceof Block) {
                    TorcherinoAPI.INSTANCE.blacklistBlock((Block) value);
                } else {
                    LOGGER.error("Received blacklist_block message with invalid value, must be either a Block or ResourceLocation.");
                }
            } else if (method.equals("blacklist_tile")) {
                if (value instanceof ResourceLocation) {
                    TorcherinoAPI.INSTANCE.blacklistBlockEntity((ResourceLocation) value);
                } else if (value instanceof BlockEntityType) {
                    TorcherinoAPI.INSTANCE.blacklistBlockEntity((BlockEntityType<?>) value);
                } else {
                    LOGGER.error("Received blacklist_tile message with invalid value, must be either a TileEntityType or ResourceLocation.");
                }
            } else {
                LOGGER.error("Received IMC message with invalid method, must be either: \"blacklist_block\" or \"blacklist_tile\".");
            }
        });
    }
}
