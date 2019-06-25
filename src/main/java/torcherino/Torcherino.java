package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import torcherino.blocks.ModBlocks;
import torcherino.config.ConfigManager;
import torcherino.config.TorcherinoConfig;
import torcherino.items.ModItems;
import torcherino.network.Networker;
import java.nio.file.Path;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		TorcherinoTiers.INSTANCE.initialise();
		Path configPath = FMLPaths.CONFIGDIR.get().resolve("sci4me/Torcherino.cfg");
		Utilities.LOGGER.info(configPath.toString());
		TorcherinoConfig.INSTANCE = ConfigManager.loadConfig(TorcherinoConfig.class, configPath);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 4, 1);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		Networker.INSTANCE.initialise();
	}
}