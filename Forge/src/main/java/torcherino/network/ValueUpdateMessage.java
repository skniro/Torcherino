package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.LogicalSide;
import torcherino.Torcherino;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public record ValueUpdateMessage(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) implements CustomPacketPayload {
    private static final ResourceLocation UPDATE_TORCHERINO_VALUES = Torcherino.resloc("update_torcherino_values");
    public static final Type<ValueUpdateMessage> TYPE = new Type<>(UPDATE_TORCHERINO_VALUES);
    public static final StreamCodec<FriendlyByteBuf, ValueUpdateMessage> CODEC = CustomPacketPayload.codec(ValueUpdateMessage::encode, ValueUpdateMessage::decode);


    public static void encode(ValueUpdateMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos).writeInt(message.xRange).writeInt(message.zRange).writeInt(message.yRange).writeInt(message.speed).writeInt(message.redstoneMode);
    }

    public static ValueUpdateMessage decode(FriendlyByteBuf buffer) {
        return new ValueUpdateMessage(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    public static void handle(ValueUpdateMessage message, CustomPayloadEvent.Context contextSupplier) {
        CustomPayloadEvent.Context context = contextSupplier;
            context.enqueueWork(() -> {
                if (context.getSender().level().getBlockEntity(message.pos) instanceof TorcherinoBlockEntity blockEntity) {
                    if (!blockEntity.readClientData(message.xRange, message.zRange, message.yRange, message.speed, message.redstoneMode)) {
                        Torcherino.LOGGER.error("Data received from " + context.getSender().getName().getString() + "(" + context.getSender().getStringUUID() + ") is invalid.");
                    }
                }
            });
            context.setPacketHandled(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
