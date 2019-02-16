package torcherino.Blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
	public static void onBlockRegistry( RegistryEvent.Register<Block> registryEvent)
	{
		IForgeRegistry<Block> registry = registryEvent.getRegistry();
		register(registry, "");
		register(registry, "compressed_");
		register(registry, "double_compressed_");
	}

	private static void register(IForgeRegistry<Block> registry, String name)
	{
		BlockTorcherino blockTorcherino = new BlockTorcherino();
		BlockTorcherinoWall blockTorcherinoWall = new BlockTorcherinoWall(blockTorcherino);
		Item itemTorcherino = new ItemWallOrFloor(blockTorcherino, blockTorcherinoWall, new Item.Properties().group(ItemGroup.FOOD));
		itemTorcherino.setRegistryName(Utils.getId(name+"torcherino"));
		blockTorcherino.setRegistryName(Utils.getId(name+"torcherino"));
		blockTorcherinoWall.setRegistryName(Utils.getId("wall_"+name+"torcherino"));
		registry.register(blockTorcherino);
		registry.register(blockTorcherinoWall);
		ModItems.items.add(itemTorcherino);
	}

}
