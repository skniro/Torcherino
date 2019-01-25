package torcherino;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import org.dimdev.rift.listener.client.KeyBindingAdder;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.Collection;

public class TorcherinoClientListener implements KeybindHandler, KeyBindingAdder
{
    private static KeyBinding TORCHERINO_KEYBIND;
    private boolean pressed = false;

    public void processKeybinds()
    {
        boolean keyBindPressed = InputMappings.isKeyDown(InputMappings.getInputByName(TORCHERINO_KEYBIND.getTranslationKey()).getKeyCode());
        if((keyBindPressed && !pressed) || (!keyBindPressed && pressed))
        {
            pressed = !pressed;
            TorcherinoMessage message = new TorcherinoMessage(pressed);
            message.sendToServer();
        }
    }

    public Collection<? extends KeyBinding> getKeyBindings()
    {
        Collection<KeyBinding> collection = new ArrayList<>();
        TORCHERINO_KEYBIND = new KeyBinding("key.torcherino.modifier", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.misc");
        collection.add(TORCHERINO_KEYBIND);
        return collection;
    }
}
