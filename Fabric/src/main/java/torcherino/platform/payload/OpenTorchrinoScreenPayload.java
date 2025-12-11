package torcherino.platform.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import torcherino.Torcherino;

public record OpenTorchrinoScreenPayload(BlockPos blockPos, String title,  int xRange, int zRange, int yRange, int speed, int redstoneMode, FriendlyByteBuf buf) implements CustomPacketPayload {
    public static final Identifier OPEN_TORCHERINO_SCREEN = Torcherino.resloc("open_torcherino_screen");
    public static final Type<OpenTorchrinoScreenPayload> TYPE = new Type<>(OPEN_TORCHERINO_SCREEN);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTorchrinoScreenPayload> CODEC = CustomPacketPayload.codec(OpenTorchrinoScreenPayload::write, OpenTorchrinoScreenPayload::new);

    public OpenTorchrinoScreenPayload(final FriendlyByteBuf buf){
        this(buf.readBlockPos(),
             buf.readUtf(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.retain()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeUtf(String.valueOf(title));
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }

    public void retain() {
        buf.retain();
    }

    public void release() {
        buf.release();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
