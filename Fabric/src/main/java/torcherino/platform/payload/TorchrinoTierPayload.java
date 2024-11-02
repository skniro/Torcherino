package torcherino.platform.payload;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import torcherino.Torcherino;
import torcherino.api.Tier;
import com.mojang.datafixers.util.Pair;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.Map;

/*public record TorchrinoTierPayload(int size, ResourceLocation resourceLocation, int maxSpeed, int xzRange, int yRange) implements CustomPacketPayload {
    private static final ResourceLocation TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    public static final CustomPacketPayload.Type<TorchrinoTierPayload> TYPE = new CustomPacketPayload.Type<>(TORCHERINO_TIER_SYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, TorchrinoTierPayload> CODEC = CustomPacketPayload.codec(TorchrinoTierPayload::write,TorchrinoTierPayload::new);

    public TorchrinoTierPayload(final FriendlyByteBuf buf){
        this(buf.readInt(),
             buf.readResourceLocation(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(size);
        buffer.writeResourceLocation(resourceLocation);
        buffer.writeInt(maxSpeed);
        buffer.writeInt(xzRange);
        buffer.writeInt(yRange);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}*/

public record TorchrinoTierPayload(Map<ResourceLocation, Tier> tiers) implements CustomPacketPayload {
    private static final ResourceLocation TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    public static final Type<TorchrinoTierPayload> TYPE = new Type<>(TORCHERINO_TIER_SYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, TorchrinoTierPayload> CODEC = CustomPacketPayload.codec(TorchrinoTierPayload::write, TorchrinoTierPayload::decode);

    public static void write(TorchrinoTierPayload message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.tiers.size());
        message.tiers.forEach((name, tier) -> TorchrinoTierPayload.writeTier(name, tier, buffer));
    }

    public static TorchrinoTierPayload decode(FriendlyByteBuf buffer) {
        Map<ResourceLocation, Tier> localTiers = new HashMap<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            Pair<ResourceLocation, Tier> entry = TorchrinoTierPayload.readTier(buffer);
            localTiers.put(entry.getFirst(), entry.getSecond());
        }
        return new TorchrinoTierPayload(localTiers);
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
