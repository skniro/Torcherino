package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import torcherino.blocks.ModBlocks;
import torcherino.client.TorcherinoClient;
import torcherino.items.ModItems;
import torcherino.network.Networker;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		TorcherinoTiers.INSTANCE.initialise();
		// todo load this from config:
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("normal"), 4, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("compressed"), 36, 4, 1);
		TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc("double_compressed"), 324, 4, 1);
		ModBlocks.INSTANCE.initialise();
		eventBus.register(ModBlocks.INSTANCE);
		eventBus.register(ModItems.INSTANCE);
		if (FMLEnvironment.dist.isClient()) TorcherinoClient.registerGUIExtensionPoint();
		Networker.INSTANCE.initialise();
	}
}