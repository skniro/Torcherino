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

@Mod.EventBusSubscriber(modid=Utils.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
	private static IForgeRegistry<Block> registry;

	@SubscribeEvent public static void onBlockRegistry(final RegistryEvent.Register<Block> registryEvent)
	{
		registry = registryEvent.getRegistry();
		register("", 4);
		register("compressed_", 36);
		register("double_compressed_", 324);
	}

	private static void register(String name, int speed)
	{
		Block blockTorcherino = new BlockTorcherino(speed).setRegistryName(Utils.getId(name+"torcherino"));
		Block blockTorcherinoWall = new BlockTorcherinoWall(blockTorcherino).setRegistryName(Utils.getId("wall_"+name+"torcherino"));
		Block blockLanterino = new BlockLanterino(speed).setRegistryName(Utils.getId(name+"lanterino"));
		Item itemTorcherino = new ItemWallOrFloor(blockTorcherino, blockTorcherinoWall, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(Utils.getId(name+"torcherino"));
		Item itemLanterino = new ItemBlock(blockLanterino, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(Utils.getId(name+"lanterino"));
		registry.registerAll(blockTorcherino, blockTorcherinoWall, blockLanterino);
		Utils.blacklistBlocks(blockTorcherino, blockTorcherinoWall, blockLanterino);
		ModItems.items.add(itemTorcherino);
		ModItems.items.add(itemLanterino);
	}
}
