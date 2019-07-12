package torcherino.blocks;

import net.minecraft.block.Block;
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
import torcherino.api.blocks.WallTorcherinoBlock;
import torcherino.api.blocks.entity.TocherinoBlockEntityType;
import torcherino.api.blocks.entity.TorcherinoBlockEntity;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public class ModBlocks
{
    public static final ModBlocks INSTANCE = new ModBlocks();
    private HashMap<Identifier, Block> blocks;
    private HashMap<Identifier, Item> items;

    public void initialize()
    {
        blocks = new HashMap<>();
        items = new HashMap<>();
        TorcherinoAPI.INSTANCE.getTiers().forEach(this::createBlocks);
        registerBlocks();
        registerItems();
        registerBlockEntity();
    }

    private void createBlocks(Identifier tierID, Tier tier)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            Identifier torcherinoID = getIdentifier(tierID, "torcherino");
            Identifier lanterinoID = getIdentifier(tierID, "lanterino");
            Block torcherinoBlock = new TorcherinoBlock(tierID);
            Block torcherinoWallBlock = new WallTorcherinoBlock(tierID, new Identifier(Torcherino.MOD_ID, "blocks/" + torcherinoID.getPath()));
            Block lanterinoBlock = new LanterinoBlock(tierID);
            blocks.put(torcherinoID, torcherinoBlock);
            blocks.put(new Identifier(Torcherino.MOD_ID, "wall_" + torcherinoID.getPath()), torcherinoWallBlock);
            blocks.put(lanterinoID, lanterinoBlock);
            Item torcherinoItem = new WallStandingBlockItem(torcherinoBlock, torcherinoWallBlock, new Item.Settings().group(ItemGroup.DECORATIONS));
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS));
            items.put(torcherinoID, torcherinoItem);
            items.put(lanterinoID, lanterinoItem);
            TorcherinoAPI.INSTANCE.blacklistBlock(torcherinoBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(torcherinoWallBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(lanterinoBlock);
        }
    }

    private void registerBlocks() { blocks.forEach((id, block) -> Registry.register(Registry.BLOCK, id, block)); }

    private void registerItems() { items.forEach((id, item) -> Registry.register(Registry.ITEM, id, item)); }

    private void registerBlockEntity()
    {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(Torcherino.MOD_ID, "torcherino"),
                new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null, null));
    }

    private Identifier getIdentifier(Identifier tierID, String type)
    {
        if (tierID.getPath().equals("normal")) return new Identifier(Torcherino.MOD_ID, type);
        return new Identifier(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
