package torcherino.platform.payload;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import torcherino.Torcherino;
import torcherino.api.Tier;

import java.util.HashMap;
import java.util.Map;

public record TorchrinoTierPayload(Map<Identifier, Tier> tiers) implements CustomPacketPayload {
    private static final Identifier TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    public static final Type<TorchrinoTierPayload> TYPE = new Type<>(TORCHERINO_TIER_SYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, TorchrinoTierPayload> CODEC = CustomPacketPayload.codec(TorchrinoTierPayload::write, TorchrinoTierPayload::decode);

    public static void write(TorchrinoTierPayload message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.tiers.size());
        message.tiers.forEach((name, tier) -> TorchrinoTierPayload.writeTier(name, tier, buffer));
    }

    public static TorchrinoTierPayload decode(FriendlyByteBuf buffer) {
        Map<Identifier, Tier> localTiers = new HashMap<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            Pair<Identifier, Tier> entry = TorchrinoTierPayload.readTier(buffer);
            localTiers.put(entry.getFirst(), entry.getSecond());
        }
        return new TorchrinoTierPayload(localTiers);
    }



    private static Pair<Identifier, Tier> readTier(FriendlyByteBuf buffer) {
        return new Pair<>(buffer.readIdentifier(), new Tier(buffer.readInt(), buffer.readInt(), buffer.readInt()));
    }

    private static void writeTier(Identifier name, Tier tier, FriendlyByteBuf buffer) {
        buffer.writeIdentifier(name).writeInt(tier.maxSpeed()).writeInt(tier.xzRange()).writeInt(tier.yRange());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
