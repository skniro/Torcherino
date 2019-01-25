package torcherino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import torcherino.networking.ClientTickHandler;

@Environment(EnvType.CLIENT)
public class ClientTorcherino implements ClientModInitializer
{
    public static final FabricKeyBinding torcherinoKeyBind = FabricKeyBinding.Builder.create(Utils.getId("modifier"),
            InputUtil.Type.KEY_KEYBOARD, GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.misc").build();

    @Override
    public void onInitializeClient()
    {
        KeyBindingRegistry.INSTANCE.register(torcherinoKeyBind);
        ClientTickEvent.CLIENT.register(new ClientTickHandler());
    }

}
