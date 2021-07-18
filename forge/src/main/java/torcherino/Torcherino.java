package torcherino;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.platform.NetworkUtilsImpl;

@Mod(Torcherino.MOD_ID)
public final class Torcherino {
    public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
    public static final String MOD_ID = "torcherino";

    public Torcherino() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.initialize();
        ModContent.initialise(eventBus);
        NetworkUtilsImpl.getInstance().initialize();
        eventBus.addListener(this::processIMC);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.WATER);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.LAVA);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.CAVE_AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.VOID_AIR);
    }

    public static ResourceLocation getRl(String path) {
        return new ResourceLocation(MOD_ID, path);
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
