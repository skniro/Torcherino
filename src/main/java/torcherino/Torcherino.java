package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.config.Config;

@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer.class)
public class Torcherino implements ModInitializer, ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger("torcherino");
    @Override
    public void onInitialize()
    {
        Config.initialize();
    }

    @Override
    public void onInitializeClient()
    {

    }
}
