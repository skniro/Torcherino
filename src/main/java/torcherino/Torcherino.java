package torcherino;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.GameData;
import torcherino.Blocks.ModBlocks;
import torcherino.Blocks.Tiles.TileEntityTorcherino;
import torcherino.Items.ModItems;
import torcherino.network.Client;
import torcherino.network.Messages;

@Mod("torcherino")
public class Torcherino
{
	public static SimpleChannel torcherinoNetworkChannel = NetworkRegistry.ChannelBuilder.named(Utils.getId("modifier"))
			.networkProtocolVersion(() -> "1.0")
			.clientAcceptedVersions((String version) -> version.equals("1.0"))
			.serverAcceptedVersions((String version) -> version.equals("1.0"))
			.simpleChannel();
	public static TileEntityType TORCHERINO_TILE_ENTITY_TYPE;
	// todo: move to sided proxy
	// also move to using @Mod.EventBusSubscriber instead of modEventBus.register
	public Torcherino()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TorcherinoConfig.commonSpec);
		torcherinoNetworkChannel.registerMessage(0, Messages.KeystateUpdate.class, Messages.KeystateUpdate::encode, Messages.KeystateUpdate::decode, Messages.KeystateUpdate::handle);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(Client.class);
		modEventBus.addListener(this::processIMC);
		modEventBus.register(ModBlocks.class);
		modEventBus.register(ModItems.class);
		modEventBus.addListener((final RegistryEvent.Register<TileEntityType<?>> registryEvent) -> {
			if(registryEvent.getName() != GameData.TILEENTITIES) return;
			TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TileEntityTorcherino::new).build(null);
			TORCHERINO_TILE_ENTITY_TYPE.setRegistryName(Utils.getId("torcherino"));
			registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
		});
		MinecraftForge.EVENT_BUS.register(Client.class);
		Utils.blacklistTileEntity(TileEntityTorcherino.class);
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