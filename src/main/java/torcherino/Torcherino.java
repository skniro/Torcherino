package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import torcherino.blocks.ModBlocks;
import torcherino.config.TorcherinoConfig;
import torcherino.items.ModItems;
import torcherino.network.Networker;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		TorcherinoConfig.initialise();
		TorcherinoTiers.INSTANCE.initialise();
		ModBlocks.INSTANCE.initialise();
		Networker.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);

	}
}