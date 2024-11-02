package torcherino.network;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import torcherino.Torcherino;
import torcherino.api.Tier;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public record S2CTierSyncMessage(Map<ResourceLocation, Tier> tiers) implements CustomPacketPayload {
    private static final ResourceLocation TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    public static final Type<S2CTierSyncMessage> TYPE = new Type<>(TORCHERINO_TIER_SYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CTierSyncMessage> CODEC = CustomPacketPayload.codec(S2CTierSyncMessage::write, S2CTierSyncMessage::decode);

    public static void write(S2CTierSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.tiers.size());
        message.tiers.forEach((name, tier) -> S2CTierSyncMessage.writeTier(name, tier, buffer));
    }

    public static S2CTierSyncMessage decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, Tier> localTiers = new HashMap<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            Pair<ResourceLocation, Tier> entry = S2CTierSyncMessage.readTier(buffer);
            localTiers.put(entry.getFirst(), entry.getSecond());
        }
        return new S2CTierSyncMessage(localTiers);
    }



    private static Pair<ResourceLocation, Tier> readTier(FriendlyByteBuf buffer) {
        return new Pair<>(buffer.readResourceLocation(), new Tier(buffer.readInt(), buffer.readInt(), buffer.readInt()));
    }

    private static void writeTier(ResourceLocation name, Tier tier, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(name).writeInt(tier.maxSpeed()).writeInt(tier.xzRange()).writeInt(tier.yRange());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
