package torcherino;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.network.Networker;

@Mod(Torcherino.MOD_ID)
public class Torcherino
{
    public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
    public static final String MOD_ID = "torcherino";

    public Torcherino()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.initialise();
        ModContent.INSTANCE.initialise();
        Networker.INSTANCE.initialise();
        eventBus.register(ModContent.INSTANCE);
        eventBus.addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.addListener(this::processPlayerJoin);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.WATER);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.LAVA);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.CAVE_AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.VOID_AIR);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static ResourceLocation resloc(String path) { return new ResourceLocation(MOD_ID, path); }

    private void processPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) { Networker.INSTANCE.sendServerTiers((ServerPlayerEntity) event.getPlayer()); }

    @SubscribeEvent
    public void processIMC(final InterModProcessEvent event)
    {
        event.getIMCStream().forEach((message) ->
        {
            String method = message.getMethod();
            Object value = message.getMessageSupplier().get();
            if (method.equals("blacklist_block"))
            {
                if (value instanceof ResourceLocation) { TorcherinoAPI.INSTANCE.blacklistBlock((ResourceLocation) value); }
                else if (value instanceof Block) TorcherinoAPI.INSTANCE.blacklistBlock((Block) value);
                else
                {
                    LOGGER.error("Received blacklist_block message with invalid value, must be either a Block or ResourceLocation.");
                }
            }
            else if (method.equals("blacklist_tile"))
            {
                if (value instanceof ResourceLocation) { TorcherinoAPI.INSTANCE.blacklistTileEntity((ResourceLocation) value); }
                else if (value instanceof TileEntityType) TorcherinoAPI.INSTANCE.blacklistTileEntity((TileEntityType<?>) value);
                else
                {
                    LOGGER.error("Received blacklist_tile message with invalid value, must be either a TileEntityType or ResourceLocation.");
                }
            }
            else
            {
                LOGGER.error("Received IMC message with invalid method, must be either: \"blacklist_block\" or \"blacklist_tile\".");
            }
        });
    }

    /*
     * TODO: Move over to using https://mcforge.readthedocs.io/en/1.13.x/gettingstarted/dependencymanagement/
     */
}