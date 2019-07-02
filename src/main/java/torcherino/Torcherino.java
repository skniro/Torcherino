package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.blocks.Blocks;
import torcherino.config.Config;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class Torcherino implements ModInitializer, ClientModInitializer
{
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize()
    {
        Torcherino.LOGGER.info("Hey were loaded, lets define our blocks and stuff.");
        Config.initialize();

        Blocks.INSTANCE.initialise();
        Blocks.INSTANCE.registerBlockEntity();
    }

    @Override
    public void onInitializeClient()
    {

    }
}
