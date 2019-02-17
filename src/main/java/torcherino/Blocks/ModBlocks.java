package torcherino.Blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemWallOrFloor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import torcherino.Items.ModItems;
import torcherino.Utils;

@Mod.EventBusSubscriber( modid = "torcherino", bus = Mod.EventBusSubscriber.Bus.MOD )
public class ModBlocks
{
	@SubscribeEvent
	public static void onBlockRegistry(final RegistryEvent.Register<Block> registryEvent)
	{
		IForgeRegistry<Block> registry = registryEvent.getRegistry();
		register(registry, "", 4);
		register(registry, "compressed_", 36);
		register(registry, "double_compressed_", 324);
	}

	private static void register(IForgeRegistry<Block> registry, String name, int speed)
	{
		BlockTorcherino blockTorcherino = new BlockTorcherino(speed);
		BlockTorcherinoWall blockTorcherinoWall = new BlockTorcherinoWall(blockTorcherino);
		BlockLanterino blockLanterino = new BlockLanterino(speed);
		Item itemTorcherino = new ItemWallOrFloor(blockTorcherino, blockTorcherinoWall, new Item.Properties().group(ItemGroup.DECORATIONS));
		Item itemLanterino = new ItemBlock(blockLanterino, new Item.Properties().group(ItemGroup.DECORATIONS));
		itemTorcherino.setRegistryName(Utils.getId(name+"torcherino"));
		itemLanterino.setRegistryName(Utils.getId(name+"lanterino"));
		blockTorcherino.setRegistryName(Utils.getId(name+"torcherino"));
		blockTorcherinoWall.setRegistryName(Utils.getId("wall_"+name+"torcherino"));
		blockLanterino.setRegistryName(Utils.getId(name+"lanterino"));
		registry.register(blockTorcherino);
		registry.register(blockTorcherinoWall);
		registry.register(blockLanterino);
		Utils.blacklistBlock(blockTorcherino);
		Utils.blacklistBlock(blockTorcherinoWall);
		Utils.blacklistBlock(blockLanterino);
		ModItems.items.add(itemTorcherino);
		ModItems.items.add(itemLanterino);
	}
}
