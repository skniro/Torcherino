package torcherino.platform.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.platform.NetworkUtilsImpl;

import java.util.HashMap;

public record UpdateTorchrinoPayload(BlockPos blockPos, int xRange, int zRange, int yRange, int speed, int redstoneMode) implements CustomPacketPayload {
    private static final ResourceLocation UPDATE_TORCHERINO_VALUES = Torcherino.resloc("update_torcherino_values");
    public static final Type<UpdateTorchrinoPayload> TYPE = new Type<>(UPDATE_TORCHERINO_VALUES);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTorchrinoPayload> CODEC = CustomPacketPayload.codec(UpdateTorchrinoPayload::write, UpdateTorchrinoPayload::new);

    public UpdateTorchrinoPayload(final FriendlyByteBuf buf){
        this(buf.readBlockPos(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
