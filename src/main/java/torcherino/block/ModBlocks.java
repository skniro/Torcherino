package torcherino.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.item.block.WallStandingBlockItem;
import net.minecraft.util.registry.Registry;
import torcherino.Utils;

public class ModBlocks
{
	public static void onInitialize()
	{
		register("", 4);
		register("compressed_", 36);
		register("double_compressed_", 324);
	}

	private static void register(String name, int speed)
	{
		Block torcherinoBlock = new TorcherinoBlock(speed, Utils.getId("block/%storcherino", name));
		Block torcherinoWallBlock = new TorcherinoWallBlock(speed, torcherinoBlock);
		Block lanterinoBlock = new LanterinoBlock(speed, Utils.getId("block/%slanterino", name));
		Registry.register(Registry.BLOCK, Utils.getId("%storcherino", name), torcherinoBlock);
		Registry.register(Registry.BLOCK, Utils.getId("wall_%storcherino", name), torcherinoWallBlock);
		Registry.register(Registry.ITEM, Utils.getId("%storcherino", name), new WallStandingBlockItem(torcherinoBlock,
				torcherinoWallBlock, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
		Registry.register(Registry.BLOCK, Utils.getId("%slanterino", name), lanterinoBlock);
		Registry.register(Registry.ITEM, Utils.getId("%slanterino", name), new BlockItem(lanterinoBlock,
				new Item.Settings().itemGroup(ItemGroup.BUILDING_BLOCKS)));
		Utils.blacklistBlock(torcherinoBlock);
		Utils.blacklistBlock(torcherinoWallBlock);
		Utils.blacklistBlock(lanterinoBlock);
	}
}
