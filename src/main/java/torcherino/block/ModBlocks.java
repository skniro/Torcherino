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
        Block TorcherinoBlock = new TorcherinoBlock(speed, Utils.getId("block/%storcherino", name));
        Block TorcherinoWallBlock = new TorcherinoWallBlock(speed, TorcherinoBlock);
        Block LanterinoBlock = new LanterinoBlock(speed, Utils.getId("block/%slanterino", name));
        Registry.register(Registry.BLOCK, Utils.getId("%storcherino", name), TorcherinoBlock);
        Registry.register(Registry.BLOCK, Utils.getId("wall_%storcherino", name), TorcherinoWallBlock);
        Registry.register(Registry.ITEM, Utils.getId("%storcherino", name), new WallStandingBlockItem(TorcherinoBlock,
                TorcherinoWallBlock, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
        Registry.register(Registry.BLOCK, Utils.getId("%slanterino", name), LanterinoBlock);
        Registry.register(Registry.ITEM, Utils.getId("%slanterino", name), new BlockItem(LanterinoBlock,
                new Item.Settings().itemGroup(ItemGroup.BUILDING_BLOCKS)));
        Utils.blacklistBlock(TorcherinoBlock);
        Utils.blacklistBlock(TorcherinoWallBlock);
        Utils.blacklistBlock(LanterinoBlock);
    }
}
