package torcherino.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.registry.Registry;
import torcherino.Utils;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.LanterinoBlock;
import torcherino.api.blocks.TorcherinoBlock;
import torcherino.api.blocks.TorcherinoBlockEntity;
import torcherino.api.blocks.TorcherinoWallBlock;

import java.util.HashSet;

public class Blocks
{
    public static BlockEntityType<TorcherinoBlockEntity> TORCHERINO_BLOCK_ENTITY_TYPE;

    private static HashSet<Block> BLOCKS = new HashSet<>();
    private static final TorcherinoAPI API = TorcherinoAPI.INSTANCE;

    public static void onInitialize()
    {
        register("", 4);
        register("compressed_", 36);
        register("double_compressed_", 324);
        TORCHERINO_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY, Utils.getId("torcherino"), BlockEntityType.Builder
                .create(TorcherinoBlockEntity::new, BLOCKS.toArray(new Block[]{})).build(null));
        API.blacklistBlockEntity(TORCHERINO_BLOCK_ENTITY_TYPE);
    }

    private static void register(String name, int speed)
    {
        Block torcherinoBlock = new TorcherinoBlock(speed, Utils.getId("block/%storcherino", name));
        Block torcherinoWallBlock = new TorcherinoWallBlock(speed, torcherinoBlock);
        Block lanterinoBlock = new LanterinoBlock(speed, Utils.getId("block/%slanterino", name));
        Registry.register(Registry.BLOCK, Utils.getId("%storcherino", name), torcherinoBlock);
        Registry.register(Registry.BLOCK, Utils.getId("wall_%storcherino", name), torcherinoWallBlock);
        Registry.register(Registry.ITEM, Utils.getId("%storcherino", name), new WallStandingBlockItem(torcherinoBlock, torcherinoWallBlock, new Item.Settings()
                .group(ItemGroup.DECORATIONS)));
        Registry.register(Registry.BLOCK, Utils.getId("%slanterino", name), lanterinoBlock);
        Registry.register(Registry.ITEM, Utils.getId("%slanterino", name), new BlockItem(lanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
        BLOCKS.add(torcherinoBlock);
        BLOCKS.add(torcherinoBlock);
        BLOCKS.add(torcherinoWallBlock);
        BLOCKS.add(lanterinoBlock);
        API.blacklistBlock(torcherinoBlock);
        API.blacklistBlock(torcherinoWallBlock);
        API.blacklistBlock(lanterinoBlock);
    }
}
