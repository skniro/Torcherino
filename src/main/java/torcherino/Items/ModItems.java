package torcherino.Items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import torcherino.Utils;

@Mod.EventBusSubscriber(modid=Utils.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
	public static OrderedHashSet<Item> items = new OrderedHashSet<>();

	@SubscribeEvent public static void onItemRegistry(final RegistryEvent.Register<Item> registryEvent)
	{
		IForgeRegistry<Item> registry = registryEvent.getRegistry();
		items.forEach(registry::register);
	}
}