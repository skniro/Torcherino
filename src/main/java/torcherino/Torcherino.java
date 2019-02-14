package torcherino;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootConditions;
import torcherino.block.ModBlocks;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.networking.TorcherinoPacketConsumer;

public class Torcherino implements ModInitializer
{
	public static final BlockEntityType<TorcherinoBlockEntity> TORCHERINO_BLOCK_ENTITY_TYPE= Registry.register(Registry.BLOCK_ENTITY,
            Utils.getId("torcherino"), BlockEntityType.Builder.create(TorcherinoBlockEntity::new).build(null));

	@Override
	public void onInitialize()
	{
		LootConditions.register(new PlayerModifierLootCondition.Factory());
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("modifier"), new TorcherinoPacketConsumer());
		ModBlocks.onInitialize();
		Utils.blacklistBlockEntity(TORCHERINO_BLOCK_ENTITY_TYPE);
	}
}
