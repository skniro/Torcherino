package torcherino.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import torcherino.blocks.Blocks;

/*
	Todo: consider moving this class into blocks or Torcherino main class.
 */
public class Items
{
	public static final Items INSTANCE = new Items();

	@SubscribeEvent public void onItemRegistry(final RegistryEvent.Register<Item> registryEvent)
	{
		registryEvent.getRegistry().registerAll(Blocks.INSTANCE.getItems().toArray(new Item[]{}));
	}
}
