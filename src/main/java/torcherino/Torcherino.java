package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import torcherino.config.Config;
import torcherino.items.Items;
import torcherino.blocks.Blocks;
import torcherino.network.Networker;

@Mod(Utilities.MOD_ID)
public class Torcherino
{
	public Torcherino()
	{
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Config.initialise();
		Blocks.INSTANCE.initialise();
		Networker.INSTANCE.initialise();
		eventBus.register(Blocks.INSTANCE);
		eventBus.register(Items.INSTANCE);
	}
}