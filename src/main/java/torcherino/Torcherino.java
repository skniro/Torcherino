package torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.blocks.misc.TorcherinoScreen;
import torcherino.blocks.misc.TorcherinoTileEntity;
import torcherino.network.Client;
import torcherino.network.Messages;

@Mod(Utils.MOD_ID)
@Mod.EventBusSubscriber(modid = Utils.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Torcherino
{
	public static SimpleChannel torcherinoNetworkChannel = NetworkRegistry.ChannelBuilder.named(Utils.getId("modifier")).networkProtocolVersion(() -> "1.0").clientAcceptedVersions((String version) -> version.equals("1.0")).serverAcceptedVersions((String version) -> version.equals("1.0")).simpleChannel();
	public static TileEntityType TORCHERINO_TILE_ENTITY_TYPE;

	public Torcherino()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TorcherinoConfig.commonSpec);
		torcherinoNetworkChannel.registerMessage(0, Messages.KeystateUpdate.class, Messages.KeystateUpdate::encode, Messages.KeystateUpdate::decode, Messages.KeystateUpdate::handle);
		Utils.blacklistTileEntity(TorcherinoTileEntity.class);
		if (FMLEnvironment.dist.isClient())
		{
			FMLJavaModLoadingContext.get().getModEventBus().register(Client.class);
			MinecraftForge.EVENT_BUS.register(Client.class);
			ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () ->
			{
				return openContainer ->
				{
					BlockPos pos = openContainer.getAdditionalData().readBlockPos();
					EntityPlayerSP player = Minecraft.getInstance().player;
					TileEntity tile = player.world.getTileEntity(pos);
					if (tile instanceof TorcherinoTileEntity)
					{
						return new TorcherinoScreen((TorcherinoTileEntity) tile);
					}
					return null;
				};
			});
		}
	}

	public static void processIMC(final InterModProcessEvent event)
	{
		// To use:
		// in InterModEnqueueEvent call
		// InterModComms.sendTo("torcherino", "blacklist", supplier);
		// where supplier has a get method which returns a String of either:
		// a block's resource location e.g. "minecraft:furnace"
		// or a tile entity class path e.g. net.minecraft.tileentity.TileEntityFurnace
		event.getIMCStream().forEach((InterModComms.IMCMessage message) ->
		{
			if (message.getMethod().equalsIgnoreCase("blacklist")) Utils.blacklistString((String) message.getMessageSupplier().get());
		});
	}

	@SubscribeEvent public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> registryEvent)
	{
		TORCHERINO_TILE_ENTITY_TYPE = TileEntityType.Builder.create(TorcherinoTileEntity::new).build(null).setRegistryName(Utils.getId("torcherino"));
		registryEvent.getRegistry().register(TORCHERINO_TILE_ENTITY_TYPE);
	}
}