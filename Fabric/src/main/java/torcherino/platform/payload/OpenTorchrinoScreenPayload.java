package torcherino.platform.payload;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import torcherino.Torcherino;
import torcherino.platform.NetworkUtilsImpl;

public record OpenTorchrinoScreenPayload(BlockPos blockPos, Component title,  int xRange, int zRange, int yRange, int speed, int redstoneMode, FriendlyByteBuf buf) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_TORCHERINO_SCREEN = Torcherino.resloc("open_torcherino_screen");
    public static final Type<OpenTorchrinoScreenPayload> TYPE = new Type<>(OPEN_TORCHERINO_SCREEN);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTorchrinoScreenPayload> CODEC = CustomPacketPayload.codec(OpenTorchrinoScreenPayload::write, OpenTorchrinoScreenPayload::new);

    public OpenTorchrinoScreenPayload(final FriendlyByteBuf buf){
        this(buf.readBlockPos(),
             Component.nullToEmpty(buf.readUtf()),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.readInt(),
             buf.retain()
        );
        ;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeUtf(String.valueOf(title));
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
        buffer.retain();
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
