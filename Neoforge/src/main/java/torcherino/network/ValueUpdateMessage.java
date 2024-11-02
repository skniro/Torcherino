package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import torcherino.Torcherino;
import torcherino.block.entity.TorcherinoBlockEntity;

@SuppressWarnings("ClassCanBeRecord")
public record ValueUpdateMessage(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) implements CustomPacketPayload{
    private static final ResourceLocation UPDATE_TORCHERINO_VALUES = Torcherino.resloc("update_torcherino_values");
    public static final Type<ValueUpdateMessage> TYPE = new Type<>(UPDATE_TORCHERINO_VALUES);
    public static final StreamCodec<RegistryFriendlyByteBuf, ValueUpdateMessage> CODEC = CustomPacketPayload.codec(ValueUpdateMessage::write, ValueUpdateMessage::new);

    public ValueUpdateMessage(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
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
