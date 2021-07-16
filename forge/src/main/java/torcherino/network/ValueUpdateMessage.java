package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class ValueUpdateMessage {
    private final BlockPos pos;
    private final int xRange, zRange, yRange, speed, redstoneMode;

    public ValueUpdateMessage(final BlockPos pos, final int xRange, final int zRange, final int yRange, final int speed, final int redstoneMode) {
        this.pos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    public static void encode(final ValueUpdateMessage message, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos).writeInt(message.xRange).writeInt(message.zRange).writeInt(message.yRange).writeInt(message.speed)
              .writeInt(message.redstoneMode);
    }

    public static ValueUpdateMessage decode(final FriendlyByteBuf buffer) {
        return new ValueUpdateMessage(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    public static void handle(final ValueUpdateMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender().level.getBlockEntity(message.pos) instanceof TorcherinoBlockEntity blockEntity) {
                if (!blockEntity.readClientData(message.xRange, message.zRange, message.yRange, message.speed, message.redstoneMode)) {
                    Torcherino.LOGGER.error("Data received from " + context.getSender().getName().getString() + "(" + context.getSender().getStringUUID() + ") is invalid.");
                }
            }
        });
        context.setPacketHandled(true);
    }

    private boolean withinBounds(final Tier tier) {
        if (xRange > tier.getXZRange() || zRange > tier.getXZRange() || yRange > tier.getYRange() || speed > tier.getMaxSpeed() || redstoneMode > 3 ||
                xRange < 0 || zRange < 0 || yRange < 0 || speed < 1 || redstoneMode < 0) {
            Torcherino.LOGGER.error("Data received from client is invalid.");
            return false;
        }
        return true;
    }
}
