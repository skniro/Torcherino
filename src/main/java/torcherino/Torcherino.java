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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Path sci4meDirectory = FMLPaths.CONFIGDIR.get().resolve("sci4me");
		if (!sci4meDirectory.toFile().exists())
		{
			try
			{
				Files.createDirectory(sci4meDirectory);
				TorcherinoConfig.INSTANCE = ConfigManager.loadConfig(TorcherinoConfig.class, sci4meDirectory.resolve("Torcherino.cfg").toFile());
			}
			catch (IOException e)
			{
				Utilities.LOGGER.info("Failed to create sci4me folder, config won't be saved.");
			}
		}
		TorcherinoTiers.INSTANCE.initialise();
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 4, 1);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		Networker.INSTANCE.initialise();
	}
}