package torcherino;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.block.api.LanterinoOxidizableRegistry;
import torcherino.config.Config;
import torcherino.platform.NetworkUtilsImpl;

@Mod(Torcherino.MOD_ID)
public final class Torcherino {
    public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
    public static final String MOD_ID = "torcherino";

    public static Identifier resloc(String path) {
        return Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, path);
    }

    public static ResourceKey<Block> KeyofBlock(String path) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, path));
    }

    public static ResourceKey<Item> KeyofItem(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, path));
    }

    public Torcherino(FMLJavaModLoadingContext context) {
        final var eventBus = context.getModBusGroup();
        Config.initialize();
        ModContent.initialise(eventBus);
        NetworkUtilsImpl.getInstance().initialize();
        FMLCommonSetupEvent.getBus(eventBus).addListener(this::init);
        InterModProcessEvent.getBus(eventBus).addListener(this::processIMC);
    }

    @SubscribeEvent
    public void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LanterinoOxidizableRegistry.init();
        });
    }
    public static Identifier getRl(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void processIMC(InterModProcessEvent event) {
        event.getIMCStream().forEach((message) ->
        {
            String method = message.getMethod();
            Object value = message.getMessageSupplier().get();
            if (method.equals("blacklist_block")) {
                if (value instanceof Identifier) {
                    TorcherinoAPI.INSTANCE.blacklistBlock((Identifier) value);
                } else if (value instanceof Block) {
                    TorcherinoAPI.INSTANCE.blacklistBlock((Block) value);
                } else {
                    LOGGER.error("Received blacklist_block message with invalid value, must be either a Block or Identifier.");
                }
            } else if (method.equals("blacklist_tile")) {
                if (value instanceof Identifier) {
                    TorcherinoAPI.INSTANCE.blacklistBlockEntity((Identifier) value);
                } else if (value instanceof BlockEntityType) {
                    TorcherinoAPI.INSTANCE.blacklistBlockEntity((BlockEntityType<?>) value);
                } else {
                    LOGGER.error("Received blacklist_tile message with invalid value, must be either a TileEntityType or Identifier.");
                }
            } else {
                LOGGER.error("Received IMC message with invalid method, must be either: \"blacklist_block\" or \"blacklist_tile\".");
            }
        });
    }
}
