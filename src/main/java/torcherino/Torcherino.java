package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import torcherino.block.ModBlocks;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.networking.PacketConsumers;

@EnvironmentInterface(itf=ClientModInitializer.class, value=EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer
{
	public static BlockEntityType<TorcherinoBlockEntity> TORCHERINO_BLOCK_ENTITY_TYPE;

	@Override public void onInitialize()
	{
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		TORCHERINO_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY, Utils.getId("torcherino"), BlockEntityType.Builder.create(TorcherinoBlockEntity::new).build(null));
		Utils.blacklistBlockEntity(TORCHERINO_BLOCK_ENTITY_TYPE);
		ModBlocks.onInitialize();
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}
}
