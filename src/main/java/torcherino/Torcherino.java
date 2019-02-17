package torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.GameData;
import torcherino.Blocks.ModBlocks;
import torcherino.Blocks.Tiles.TileEntityTorcherino;
import torcherino.Items.ModItems;
import torcherino.network.Client;

@Mod("torcherino")
public class Torcherino
{
	private KeyBinding modifierBind;
	public static TileEntityType TORCHERINO_TILE_ENTITY_TYPE;

	public Torcherino()
	{
		MinecraftForge.EVENT_BUS.register(Client.class);
		Utils.blacklistTileEntity(TileEntityTorcherino.class);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TorcherinoConfig.commonSpec);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(Client.class);
		modEventBus.addListener(this::processIMC);
		modEventBus.register(new ModBlocks());
		modEventBus.register(new ModItems());
		modEventBus.addListener((final RegistryEvent.Register<TileEntityType<?>> registryEvent) -> {
			if(registryEvent.getName() != GameData.TILEENTITIES) return;
			TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TileEntityTorcherino::new).build(null);
			TORCHERINO_TILE_ENTITY_TYPE.setRegistryName(Utils.getId("torcherino"));
			registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
		});
	}

	private void processIMC(final InterModProcessEvent event)
	{
		// To use:
		// in InterModEnqueueEvent call
		// InterModComms.sendTo("torcherino", "blacklist", supplier);
		// where supplier has a get method which returns a String of either:
		// a block's resource location e.g. "minecraft:furnace"
		// or a tile entity class path e.g. net.minecraft.tileentity.TileEntityFurnace
		event.getIMCStream().forEach((InterModComms.IMCMessage message) -> {
			if(message.getMethod().equalsIgnoreCase("blacklist"))
			{
				Utils.blacklistString((String) message.getMessageSupplier().get());
			}
		});
	}
}