package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import torcherino.blocks.ModBlocks;
import torcherino.items.ModItems;

@Mod(Utilities.MOD_ID)
public class Torcherino
{

	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		TorcherinoTiers.INSTANCE.initialise();
		// todo load this from config:
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 9, 3);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 9, 3);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 9, 3);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		//FMLJavaModLoadingContext.get().getModEventBus().register(this);
		//MinecraftForge.EVENT_BUS.register(this);
		//MinecraftForge.EVENT_BUS.register(ModBlocks.INSTANCE);
	}
}