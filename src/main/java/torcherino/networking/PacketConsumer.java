package torcherino.networking;

import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.util.PacketByteBuf;
import torcherino.Utils;
import java.util.function.BiConsumer;

public class PacketConsumer implements BiConsumer<PacketContext, PacketByteBuf>
{
    public void accept(PacketContext t, PacketByteBuf u)
    {
        Utils.keyStates.put(t.getPlayer(), u.readBoolean());
    }
}
