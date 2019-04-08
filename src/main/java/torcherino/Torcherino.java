package torcherino;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootConditions;
import org.lwjgl.glfw.GLFW;
import torcherino.block.ModBlocks;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.networking.ClientTickHandler;
import torcherino.networking.PacketConsumers;

@EnvironmentInterface(itf=ClientModInitializer.class, value=EnvType.CLIENT)
public class Torcherino implements ModInitializer, ClientModInitializer
{
	public static BlockEntityType<TorcherinoBlockEntity> TORCHERINO_BLOCK_ENTITY_TYPE;
	@Environment(EnvType.CLIENT) public static FabricKeyBinding MODIFIER_BIND;

	@Override public void onInitialize()
	{
		LootConditions.register(new PlayerModifierLootCondition.Factory());
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatemodifierstate"), new PacketConsumers.ModifierBindConsumer());
		ServerSidePacketRegistryImpl.INSTANCE.register(Utils.getId("updatetorcherinostate"), new PacketConsumers.UpdateTorcherinoConsumer());
		TORCHERINO_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY, Utils.getId("torcherino"), BlockEntityType.Builder.create(TorcherinoBlockEntity::new).build(null));
		Utils.blacklistBlockEntity(TORCHERINO_BLOCK_ENTITY_TYPE);
		ModBlocks.onInitialize();
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		MODIFIER_BIND = FabricKeyBinding.Builder.create(Utils.getId("modifier"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.misc").build();
		KeyBindingRegistry.INSTANCE.register(MODIFIER_BIND);
		ClientTickCallback.EVENT.register(new ClientTickHandler());
		ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreenConsumer());
	}
}
