package torcherino.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemWallOrFloor;
import torcherino.Utils;

public class ModBlocks
{
    public static void register()
    {
        registerTorcherino("", 4);
        registerTorcherino("compressed_", 36);
        registerTorcherino("double_compressed_", 324);

        registerLanterino("", 4);
        registerLanterino("compressed_", 36);
        registerLanterino("double_compressed_", 324);
    }

    private static void registerTorcherino(String name, int speed)
    {
        Block torcherinoBlock = new TorcherinoBlock(speed);
        Block torcherinoWallBlock = new TorcherinoWallBlock(speed);

        Block.register(Utils.getId("%storcherino", name), torcherinoBlock);
        Block.register(Utils.getId("wall_%storcherino", name), torcherinoWallBlock);
        Utils.registerItem(new ItemWallOrFloor(torcherinoBlock, torcherinoWallBlock, (new Item.Properties().group(ItemGroup.DECORATIONS))));
        Utils.blacklistBlock(torcherinoBlock);
        Utils.blacklistBlock(torcherinoWallBlock);
    }
    private static void registerLanterino(String name, int speed)
    {
        Block blockLanterino = new LanterinoBlock(speed);
        Block.register(Utils.getId("%slanterino", name), blockLanterino);
        Utils.registerItem(new ItemBlock(blockLanterino, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
        Utils.blacklistBlock(blockLanterino);
    }
}
