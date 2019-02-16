package torcherino.Items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;

@Mod.EventBusSubscriber( modid = "torcherino", bus = Mod.EventBusSubscriber.Bus.MOD )
public class ModItems
{
	public static HashSet<Item> items = new HashSet<>();
	@SubscribeEvent
	public static void onItemRegistry(final RegistryEvent.Register<Item> registryEvent)
	{
		IForgeRegistry<Item> registry = registryEvent.getRegistry();
		for(Item item : items)
		{
			System.out.println(item.getTranslationKey());
			registry.register(item);
		}

	}

}
