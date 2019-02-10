package torcherino.networking;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import torcherino.Utils;

public class TorcherinoPacketConsumer implements PacketConsumer
{

    public void accept(PacketContext t, PacketByteBuf u)
    {
        PlayerEntity player = t.getPlayer();
        boolean pressed = u.readBoolean();
        t.getTaskQueue().execute(() -> Utils.keyStates.put(player, pressed));
    }
}
