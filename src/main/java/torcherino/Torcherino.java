package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.blocks.Blocks;
import torcherino.config.Config;

import java.util.function.BiConsumer;

@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer.class)
public class Torcherino implements ModInitializer, ClientModInitializer
{
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize()
    {
        Torcherino.LOGGER.info("Hey were loaded, lets define our blocks and stuff.");
        Config.initialize();

        visitRegistry(Registry.BLOCK_ENTITY, ((identifier, blockEntityType) ->
        {
            Torcherino.LOGGER.info("{}: {}", identifier, blockEntityType.getClass().getCanonicalName());
        }));

        Blocks.INSTANCE.initialise();
        Blocks.INSTANCE.registerBlockEntity();
    }

    @Override
    public void onInitializeClient()
    {

    }

    public static <T> void visitRegistry(Registry<T> registry, BiConsumer<Identifier, T> visitor) {
        registry.getIds().forEach(id -> visitor.accept(id, registry.get(id)));
        RegistryEntryAddedCallback.event(registry).register((index, identifier, entry) -> visitor.accept(identifier, entry));
    }
}
