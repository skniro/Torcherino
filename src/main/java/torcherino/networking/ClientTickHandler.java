package torcherino.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.network.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.PacketByteBuf;
import torcherino.ClientTorcherino;
import torcherino.Utils;

@Environment(EnvType.CLIENT)
public class ClientTickHandler implements ClientTickCallback
{
    private boolean pressed = false;
    
    public void tick(MinecraftClient client)
    {
        if(client.getGame().getCurrentSession() == null) return;
        boolean keyBindPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(),
                InputUtil.fromName(ClientTorcherino.MODIFIER_BIND.getName()).getKeyCode());
        if(keyBindPressed ^ pressed)
        {
            PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
            buffer.writeBoolean(pressed = !pressed);
            ClientSidePacketRegistryImpl.INSTANCE.sendToServer(Utils.getId("updatemodifierstate"), buffer);
        }
    }
}
