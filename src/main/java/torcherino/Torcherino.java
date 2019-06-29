package torcherino;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import torcherino.blocks.Blocks;
import torcherino.config.Config;
import torcherino.items.Items;
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
		eventBus.addListener(this::processIMC);
	}

	@SubscribeEvent public void processIMC(final InterModProcessEvent event)
	{
		// To use: in InterModEnqueueEvent call
		// InterModComms.sendTo( MOD_ID, Method , supplier);
		// See processMessage method below for method and what they take (most likely will be a resource location)
		event.getIMCStream().forEach(this::processMessage);
	}

	public void processMessage(final InterModComms.IMCMessage message)
	{
		System.out.println(message.getMessageSupplier().get());
	}
}