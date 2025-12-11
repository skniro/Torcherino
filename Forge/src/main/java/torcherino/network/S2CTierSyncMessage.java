package torcherino.network;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import torcherino.Torcherino;
import torcherino.TorcherinoImpl;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public record S2CTierSyncMessage(Map<Identifier, Tier> tiers) implements CustomPacketPayload {
    private static final Identifier TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    public static final Type<S2CTierSyncMessage> TYPE = new Type<>(TORCHERINO_TIER_SYNC);
    public static final StreamCodec<FriendlyByteBuf, S2CTierSyncMessage> CODEC = CustomPacketPayload.codec(S2CTierSyncMessage::write, S2CTierSyncMessage::decode);

    public static void write(S2CTierSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.tiers.size());
        message.tiers.forEach((name, tier) -> writeTier(name, tier, buffer));
    }

    public static S2CTierSyncMessage decode(FriendlyByteBuf buffer) {
        Map<Identifier, Tier> localTiers = new HashMap<>();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            Pair<Identifier, Tier> entry = readTier(buffer);
            localTiers.put(entry.getFirst(), entry.getSecond());
        }
        return new S2CTierSyncMessage(localTiers);
    }

    public static void handle(S2CTierSyncMessage message, CustomPayloadEvent.Context contextSupplier) {
        CustomPayloadEvent.Context context = contextSupplier;
            context.enqueueWork(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(message.tiers));
            context.setPacketHandled(true);
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
