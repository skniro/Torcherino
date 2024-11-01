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
import torcherino.api.TorcherinoAPI;

public record TorchrinoTierPayload(int size, ResourceLocation resourceLocation, int maxSpeed, int xzRange, int yRange) implements CustomPacketPayload {
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
}
