package torcherino.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.LanterinoBlock;
import torcherino.api.blocks.TorcherinoBlock;
import torcherino.api.blocks.TorcherinoBlockEntity;
import torcherino.api.blocks.TorcherinoWallBlock;

import java.util.HashMap;

public class Blocks
{
    public static final Blocks INSTANCE = new Blocks();
    private HashMap<Identifier, Block> blocks;
    private HashMap<Identifier, Item> items;

    public void initialise()
    {
        blocks = new HashMap<>();
        items = new HashMap<>();
        TorcherinoAPI.INSTANCE.getTiers().forEach(this::createBlocks);
        this.registerBlocks();
        this.registerItems();
    }

    private void createBlocks(Identifier tierID, Tier tier)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            Identifier torcherinoID = getIdentifier(tierID, "torcherino");
            Identifier torcherinoWallID = new Identifier(Torcherino.MOD_ID, "wall_" + torcherinoID.getPath());
            Identifier lanterinoID = getIdentifier(tierID, "lanterino");

            Block torcherinoBlock = new TorcherinoBlock(tierID);
            Block torcherinoWallBlock = new TorcherinoWallBlock(tierID);
            Block lanterinoBlock = new LanterinoBlock(tierID);

            blocks.put(torcherinoID, torcherinoBlock);
            blocks.put(torcherinoWallID, torcherinoWallBlock);
            blocks.put(lanterinoID, lanterinoBlock);

            Item torcherinoItem = new WallStandingBlockItem(torcherinoBlock, torcherinoWallBlock, new Item.Settings().group(ItemGroup.DECORATIONS));
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));

            items.put(torcherinoID, torcherinoItem);
            items.put(lanterinoID, lanterinoItem);

            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(torcherinoBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(torcherinoWallBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(lanterinoBlock);

        }
    }

    private void registerBlocks()
    {
        blocks.forEach((id, block) -> { Registry.register(Registry.BLOCK, id, block); });
    }

    private void registerItems()
    {
        items.forEach((id, item) -> { Registry.register(Registry.ITEM, id, item); });
    }

    public void registerBlockEntity()
    {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(Torcherino.MOD_ID, "torcherino"), BlockEntityType.Builder
                .create(TorcherinoBlockEntity::new, TorcherinoAPI.INSTANCE.getTorcherinoBlocks().toArray(new Block[]{})).build(null));
    }

    private Identifier getIdentifier(Identifier tierID, String type)
    {
        if (tierID.getPath().equals("normal")) return new Identifier(Torcherino.MOD_ID, type);
        return new Identifier(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
