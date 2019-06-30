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
	public TileEntityType TORCHERINO_TILE_ENTITY_TYPE;
	private HashSet<Block> blocks;
	private HashSet<Item> items;

	public void initialise()
	{
		blocks = new HashSet<>();
		items = new HashSet<>();
		Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
		tiers.keySet().forEach(this::register);
	}

	private ResourceLocation getIdentifier(ResourceLocation resourceLocation, String type)
	{
		String newType = resourceLocation.getPath() + "_" + type;
		if (newType.startsWith("normal_")) newType = newType.substring(7);
		return new ResourceLocation(resourceLocation.getNamespace(), newType);
	}

	private void register(ResourceLocation resourceLocation)
	{
		if (resourceLocation.getNamespace().equals(Torcherino.MOD_ID))
		{
			ResourceLocation torcherinoID = getIdentifier(resourceLocation, "torcherino");
			ResourceLocation torcherinoWallID = Torcherino.resloc("wall_" + torcherinoID.getPath());
			ResourceLocation lanterinoID = getIdentifier(resourceLocation, "lanterino");
			Block torcherinoBlock = new TorcherinoBlock(resourceLocation).setRegistryName(torcherinoID);
			Block torcherinoWallBlock = new TorcherinoWallBlock((TorcherinoBlock) torcherinoBlock).setRegistryName(torcherinoWallID);
			Item torcherinoItem = new WallOrFloorItem(torcherinoBlock, torcherinoWallBlock, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(torcherinoID);
			Block lanterinoBlock = new LanterinoBlock(resourceLocation, torcherinoItem).setRegistryName(lanterinoID);
			Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(lanterinoID);
			blocks.add(torcherinoBlock);
			blocks.add(torcherinoWallBlock);
			blocks.add(lanterinoBlock);
			TorcherinoAPI.INSTANCE.registerTorcherinoBlock(torcherinoBlock);
			TorcherinoAPI.INSTANCE.registerTorcherinoBlock(torcherinoWallBlock);
			TorcherinoAPI.INSTANCE.registerTorcherinoBlock(lanterinoBlock);
			items.add(torcherinoItem);
			items.add(lanterinoItem);
		}
	}

	@SubscribeEvent public void onBlockRegistry(final RegistryEvent.Register<Block> registryEvent){ registryEvent.getRegistry().registerAll(blocks.toArray(new Block[]{})); }

	@SubscribeEvent public void onItemRegistry(final RegistryEvent.Register<Item> registryEvent){ registryEvent.getRegistry().registerAll(items.toArray(new Item[]{})); }

	@SubscribeEvent public void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
	{
		TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TorcherinoTileEntity::new, TorcherinoAPI.INSTANCE.getTorcherinoBlocks().toArray(new Block[]{})).build(null).setRegistryName(Torcherino.resloc("torcherino"));
		TorcherinoAPI.INSTANCE.blacklistTileEntity(TORCHERINO_TILE_ENTITY_TYPE);
		registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
	}
}
