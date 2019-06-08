package torcherino.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import torcherino.Utilities;
import torcherino.blocks.ModBlocks;

public class ModItems
{
	public static final ModItems INSTANCE = new ModItems();

	@SubscribeEvent public void onItemRegistry(final RegistryEvent.Register<Item> registryEvent)
	{
		Utilities.LOGGER.info("Registering Items.");
		registryEvent.getRegistry().registerAll(ModBlocks.INSTANCE.getItems().toArray(new Item[]{}));
	}
}
