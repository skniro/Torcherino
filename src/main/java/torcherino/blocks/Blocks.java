package torcherino.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.blocks.LanterinoBlock;
import torcherino.api.blocks.TorcherinoBlock;
import torcherino.api.blocks.TorcherinoTileEntity;
import torcherino.api.blocks.TorcherinoWallBlock;

import java.util.HashSet;
import java.util.Map;

public class Blocks
{
    public static final Blocks INSTANCE = new Blocks();
    private HashSet<Block> blocks;
    private HashSet<Item> items;

    public void initialise()
    {
        blocks = new HashSet<>();
        items = new HashSet<>();
        Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        tiers.keySet().forEach(this::register);
    }

    private ResourceLocation getIdentifier(ResourceLocation tierID, String type)
    {
        if (tierID.getPath().equals("normal")) return new ResourceLocation(Torcherino.MOD_ID, type);
        return new ResourceLocation(Torcherino.MOD_ID, tierID.getPath() + "_" + type);
    }

    private void register(ResourceLocation tierID)
    {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID))
        {
            ResourceLocation torcherinoID = getIdentifier(tierID, "torcherino");
            ResourceLocation torcherinoWallID = Torcherino.resloc("wall_" + torcherinoID.getPath());
            ResourceLocation lanterinoID = getIdentifier(tierID, "lanterino");
            Block standingBlock = new TorcherinoBlock(tierID).setRegistryName(torcherinoID);
            Block wallBlock = new TorcherinoWallBlock((TorcherinoBlock) standingBlock).setRegistryName(torcherinoWallID);
            Item torcherinoItem = new WallOrFloorItem(standingBlock, wallBlock, new Item.Properties().group(ItemGroup.DECORATIONS))
                    .setRegistryName(torcherinoID);
            Block lanterinoBlock = new LanterinoBlock(tierID).setRegistryName(lanterinoID);
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(lanterinoID);
            blocks.add(standingBlock);
            blocks.add(wallBlock);
            blocks.add(lanterinoBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(standingBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(wallBlock);
            TorcherinoAPI.INSTANCE.registerTorcherinoBlock(lanterinoBlock);
            items.add(torcherinoItem);
            items.add(lanterinoItem);
        }
    }

    @SubscribeEvent
    public void onBlockRegistry(final RegistryEvent.Register<Block> registryEvent) { registryEvent.getRegistry().registerAll(blocks.toArray(new Block[]{})); }

    @SubscribeEvent
    public void onItemRegistry(final RegistryEvent.Register<Item> registryEvent) { registryEvent.getRegistry().registerAll(items.toArray(new Item[]{})); }

    @SubscribeEvent
    public void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
    {
        TileEntityType tileEntityType = TileEntityType.Builder
                .create(TorcherinoTileEntity::new, TorcherinoAPI.INSTANCE.getTorcherinoBlocks().toArray(new Block[]{})).build(null)
                .setRegistryName(Torcherino.resloc("torcherino"));
        TorcherinoAPI.INSTANCE.blacklistTileEntity(tileEntityType);
        registryEvent.getRegistry().register(tileEntityType);
    }
}
