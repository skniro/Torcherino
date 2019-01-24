package torcherino.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.PacketByteBuf;
import torcherino.ClientTorcherino;
import torcherino.Utils;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ClientTickHandler implements Consumer<MinecraftClient>
{
    private boolean pressed = false;
    public void accept(MinecraftClient client)
    {
        if(client.getGame().getCurrentSession() == null) return;
        // detects if the key is pressed even if they is a keybind conflict.
        boolean keyBindPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(),
                InputUtil.fromName(ClientTorcherino.torcherinoKeyBind.getName()).getKeyCode());
        if((keyBindPressed && !pressed) || (!keyBindPressed && pressed))
        {
            pressed = !pressed;
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBoolean(pressed);
            client.getNetworkHandler().sendPacket(new CustomPayloadServerPacket(Utils.getId("modifier"), buffer));
        }
    }
}
