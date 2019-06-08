package torcherino.blocks;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemWallOrFloor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import torcherino.TorcherinoTiers;
import torcherino.Utilities;
import torcherino.blocks.miscellaneous.TorcherinoTileEntity;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModBlocks
{
	public static final ModBlocks INSTANCE = new ModBlocks();
	public TileEntityType TORCHERINO_TILE_ENTITY_TYPE;

	private HashSet<Block> blocks;
	private HashSet<Item> items;

	public void initialise()
	{
		blocks = new HashSet<>();
		items = new HashSet<>();
		Map<ResourceLocation, TorcherinoTiers.Tier> tiers = TorcherinoTiers.INSTANCE.getTiers();
		tiers.forEach(this::register);
		TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TorcherinoTileEntity::new).build(null).setRegistryName(Utilities.resloc("torcherino"));
	}

	private void register(ResourceLocation resourceLocation, TorcherinoTiers.Tier tier)
	{
		if (resourceLocation.getNamespace().equals(Utilities.MOD_ID))
		{
			ResourceLocation torcherinoID = Utilities.resloc(translateTierName(resourceLocation.getPath(), "torcherino"));
			ResourceLocation torcherinoWallID = Utilities.resloc("wall_" + torcherinoID.getPath());
			ResourceLocation lanterinoID = Utilities.resloc(translateTierName(resourceLocation.getPath(), "lanterino"));
			Block torcherinoBlock = new TorcherinoBlock().setRegistryName(torcherinoID);
			Block torcherinoWallBlock = new TorcherinoWallBlock().setRegistryName(torcherinoWallID);
			Block lanterinoBlock = new LanterinoBlock().setRegistryName(lanterinoID);
			Item torcherinoItem = new ItemWallOrFloor(torcherinoBlock, torcherinoWallBlock, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(torcherinoID);
			Item lanterinoItem = new ItemBlock(lanterinoBlock, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(lanterinoID);
			blocks.add(torcherinoBlock);
			blocks.add(torcherinoWallBlock);
			blocks.add(lanterinoBlock);
			items.add(torcherinoItem);
			items.add(lanterinoItem);
		}
	}

	private static String translateTierName(String name, String type)
	{
		if (name.equals("normal")) return type;
		return name + "_" + type;
	}

	@SubscribeEvent public void onBlockRegistry(final RegistryEvent.Register<Block> registryEvent)
	{
		Utilities.LOGGER.info("Registering Blocks.");
		registryEvent.getRegistry().registerAll(blocks.toArray(new Block[]{}));
	}

	@SubscribeEvent public void onTileEntityTypeRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
	{
		Utilities.LOGGER.info("Registering Tile Entities.");
		registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
	}

	public Set<Item> getItems()
	{
		return ImmutableSet.copyOf(items);
	}
}
