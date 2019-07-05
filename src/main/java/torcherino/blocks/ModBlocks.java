package torcherino.blocks;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
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
import torcherino.api.blocks.WallTorcherinoBlock;

import java.util.*;

public class ModBlocks
{
    public static final ModBlocks INSTANCE = new ModBlocks();
    private HashMap<Identifier, Block> blocks;
    private HashMap<Identifier, Item> items;
    private Set<Identifier> newBlocks;
    private Set<Block> blockEntityBlocks;

    public void initialize()
    {
        newBlocks = new HashSet<>(Registry.BLOCK.getIds());
        blockEntityBlocks = new HashSet<>();
        blocks = new HashMap<>();
        items = new HashMap<>();
        RegistryEntryAddedCallback.event(Registry.BLOCK).register((index, identifier, entry) -> newBlocks.add(identifier));
        Timer timer = new Timer();
        // todo: Find a better way of doing this.
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (newBlocks.isEmpty())
                {
                    newBlocks = null;
                    ModBlocks.this.registerBlockEntity();
                    timer.cancel();
                }
                else
                {
                    Iterator<Identifier> iterator = ImmutableSet.copyOf(newBlocks).iterator();
                    iterator.forEachRemaining((id) -> {
                        Block b = Registry.BLOCK.get(id);
                        if (b.getClass().equals(TorcherinoBlock.class) || b.getClass().equals(WallTorcherinoBlock.class) ||
                                b.getClass().equals(LanterinoBlock.class))
                        {
                            blockEntityBlocks.add(b);
                        }
                        newBlocks.remove(id);
                    });
                }
            }
        }, 0, 1500);
        TorcherinoAPI.INSTANCE.getTiers().forEach(this::createBlocks);
        registerBlocks();
        registerItems();
    }

    private void createBlocks(Identifier tierID, Tier tier)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            Identifier torcherinoID = getIdentifier(tierID, "torcherino");
            Identifier torcherinoWallID = new Identifier(Torcherino.MOD_ID, "wall_" + torcherinoID.getPath());
            Identifier lanterinoID = getIdentifier(tierID, "lanterino");

            Block torcherinoBlock = new TorcherinoBlock(tierID);
            Block torcherinoWallBlock = new WallTorcherinoBlock(tierID, new Identifier(Torcherino.MOD_ID, "blocks/" + torcherinoID.getPath()));
            Block lanterinoBlock = new LanterinoBlock(tierID);

            blocks.put(torcherinoID, torcherinoBlock);
            blocks.put(torcherinoWallID, torcherinoWallBlock);
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

    private void registerBlocks() { blocks.forEach((id, block) -> { Registry.register(Registry.BLOCK, id, block); }); }

    private void registerItems() { items.forEach((id, item) -> { Registry.register(Registry.ITEM, id, item); }); }

    private void registerBlockEntity()
    {
        Registry.register(Registry.BLOCK_ENTITY, new Identifier(Torcherino.MOD_ID, "torcherino"), BlockEntityType.Builder
                .create(TorcherinoBlockEntity::new, blockEntityBlocks.toArray(new Block[]{})).build(null));
        blockEntityBlocks = null;
    }

    private Identifier getIdentifier(Identifier tierID, String type)
    {
        if (tierID.getPath().equals("normal")) return new Identifier(Torcherino.MOD_ID, type);
        return new Identifier(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
