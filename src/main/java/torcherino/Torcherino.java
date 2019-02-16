package torcherino;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.GameData;
import torcherino.Blocks.ModBlocks;
import torcherino.Blocks.Tiles.TileEntityTorcherino;
import torcherino.Items.ModItems;
import java.util.stream.Collectors;

@Mod("torcherino")
public class Torcherino
{
	public static TileEntityType TORCHERINO_TILE_ENTITY_TYPE;
	public Torcherino()
	{
		Utils.blacklistTileEntity(TileEntityTorcherino.class);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TorcherinoConfig.commonSpec);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(TorcherinoConfig.class);
		modEventBus.addListener(this::processIMC);
		modEventBus.register(new ModBlocks());
		modEventBus.register(new ModItems());
		modEventBus.addListener((final RegistryEvent.Register<TileEntityType<?>> registryEvent) -> {
			if(registryEvent.getName() != GameData.TILEENTITIES) return;
			TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TileEntityTorcherino::new).build(null);
			TORCHERINO_TILE_ENTITY_TYPE.setRegistryName(Utils.getId("torcherino"));
			registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
		});
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void processIMC(final InterModProcessEvent event)
	{
		// some example code to receive and process InterModComms from other mods
		Utils.LOGGER.info("Got IMC", event.getIMCStream().
				map(m->m.getMessageSupplier().get()).
				collect(Collectors.toList()));


	}

}