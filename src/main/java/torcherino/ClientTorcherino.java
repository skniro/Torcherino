package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import torcherino.networking.ClientTickHandler;
import torcherino.networking.PacketConsumers;

@Environment(EnvType.CLIENT)
public class ClientTorcherino implements ClientModInitializer
{
    public static final FabricKeyBinding modifierBind= FabricKeyBinding.Builder.create(Utils.getId("modifier"),
            InputUtil.Type.KEY_KEYBOARD, GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.misc").build();

    @Override
    public void onInitializeClient()
    {
        KeyBindingRegistry.INSTANCE.register(modifierBind);
        ClientTickCallback.EVENT.register(new ClientTickHandler());
        ClientSidePacketRegistryImpl.INSTANCE.register(Utils.getId("openscreen"), new PacketConsumers.TorcherinoScreen());
    }
}
