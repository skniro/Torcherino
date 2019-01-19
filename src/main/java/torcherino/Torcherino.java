package torcherino;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootConditions;
import torcherino.block.ModBlocks;
import torcherino.block.entity.TorcherinoBlockEntity;

public class Torcherino implements ModInitializer
{
	public static final BlockEntityType<TorcherinoBlockEntity> TorcherinoBlockEntity = Registry.register(Registry.BLOCK_ENTITY, Utils.getId("torcherino"), BlockEntityType.Builder.create(torcherino.block.entity.TorcherinoBlockEntity::new).build(null));

	@Override
	public void onInitialize()
	{
		Utils.blacklistBlock(Blocks.GRASS_BLOCK);
		Utils.blacklistBlockEntity(TorcherinoBlockEntity);
		LootConditions.register(new PlayerSneakingLootCondition.Factory());
		ModBlocks.onInitialize();
	}
}
