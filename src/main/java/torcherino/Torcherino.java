package torcherino;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootConditions;
import torcherino.block.ModBlocks;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.networking.ClientTickHandler;
import torcherino.networking.PacketConsumer;

public class Torcherino implements ModInitializer
{
	public static final BlockEntityType<TorcherinoBlockEntity> TorcherinoBlockEntity = Registry.register(Registry.BLOCK_ENTITY,
            Utils.getId("torcherino"), BlockEntityType.Builder.create(torcherino.block.entity.TorcherinoBlockEntity::new).build(null));

	public static final FabricKeyBinding torcherinoKeyBind = FabricKeyBinding.Builder.create(Utils.getId("modifier"),
			InputUtil.Type.KEY_KEYBOARD, 340, "key.categories.misc").build();

	@Override
	public void onInitialize()
	{
		KeyBindingRegistry.INSTANCE.register(torcherinoKeyBind);
		ClientTickEvent.CLIENT.register(new ClientTickHandler());
		CustomPayloadPacketRegistry.SERVER.register(Utils.getId("modifier"), new PacketConsumer());

		Utils.blacklistBlock(Blocks.GRASS_BLOCK);
		Utils.blacklistBlockEntity(TorcherinoBlockEntity);

		LootConditions.register(new PlayerModifierLootCondition.Factory());
		ModBlocks.onInitialize();
	}
}
