package torcherino.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.PacketByteBuf;
import torcherino.Torcherino;
import torcherino.Utils;

import java.util.function.Consumer;

public class ClientTickHandler implements Consumer<MinecraftClient>
{
    private boolean pressed = false;
    public void accept(MinecraftClient client)
    {
        if(client.getGame().getCurrentSession() == null) return;
        // detects if the key is pressed even if they is a keybind conflict.
        boolean keyBindPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), InputUtil.fromName(Torcherino.torcherinoKeyBind.getName()).getKeyCode());
        if(keyBindPressed && !pressed)
        {
            pressed = true;
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBoolean(true);
            client.getNetworkHandler().sendPacket(new CustomPayloadServerPacket(Utils.getId("modifier"), buffer));
        }
        else if(!keyBindPressed && pressed)
        {
            pressed = false;
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBoolean(false);
            client.getNetworkHandler().sendPacket(new CustomPayloadServerPacket(Utils.getId("modifier"), buffer));
        }
    }
}
