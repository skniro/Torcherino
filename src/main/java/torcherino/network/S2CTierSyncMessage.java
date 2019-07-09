package torcherino.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.api.impl.TorcherinoImpl;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class S2CTierSyncMessage
{
    private final Map<ResourceLocation, Tier> tiers;

    public S2CTierSyncMessage(Map<ResourceLocation, Tier> tiers)
    {
        this.tiers = tiers;
    }

    static void encode(S2CTierSyncMessage msg, PacketBuffer buf)
    {
        buf.writeInt(msg.tiers.size());
        msg.tiers.forEach((name, tier) -> writeTier(name, tier, buf));
    }

    static S2CTierSyncMessage decode(PacketBuffer buf)
    {
        Map<ResourceLocation, Tier> localTiers = new HashMap<>();
        int count = buf.readInt();
        for (int i = 0; i < count; i++)
        {
            AbstractMap.SimpleImmutableEntry<ResourceLocation, Tier> entry = readTier(buf);
            localTiers.put(entry.getKey(), entry.getValue());
        }
        return new S2CTierSyncMessage(localTiers);
    }

    static void handle(S2CTierSyncMessage msg, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getOriginationSide() == LogicalSide.SERVER)
            context.enqueueWork(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(msg.tiers));
        context.setPacketHandled(true);
    }

    private static AbstractMap.SimpleImmutableEntry<ResourceLocation, Tier> readTier(PacketBuffer buf)
    {
        return new AbstractMap.SimpleImmutableEntry<>(buf.readResourceLocation(), new Tier(buf.readInt(), buf.readInt(), buf.readInt()));
    }

    private static void writeTier(ResourceLocation name, Tier tier, PacketBuffer buf)
    {
        buf.writeResourceLocation(name).writeInt(tier.getMaxSpeed()).writeInt(tier.getXZRange()).writeInt(tier.getYRange());
    }
}
