package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.tile.TorcherinoTileEntity;

import java.util.function.Supplier;

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

    static void encode(final ValueUpdateMessage message, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos).writeInt(message.xRange).writeInt(message.zRange).writeInt(message.yRange).writeInt(message.speed)
              .writeInt(message.redstoneMode);
    }

    static ValueUpdateMessage decode(final FriendlyByteBuf buffer) {
        return new ValueUpdateMessage(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @SuppressWarnings("ConstantConditions")
    static void handle(final ValueUpdateMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() ->
        {
            final Level world = context.getSender().level;
            final BlockEntity tileEntity = world.getBlockEntity(message.pos);
            if (tileEntity instanceof TorcherinoTileEntity) {
                final TorcherinoTileEntity torcherinoTileEntity = (TorcherinoTileEntity) tileEntity;
                if (message.withinBounds(TorcherinoAPI.INSTANCE.getTiers().get(torcherinoTileEntity.getTierName()))) {
                    torcherinoTileEntity.readClientData(message.xRange, message.zRange, message.yRange, message.speed, message.redstoneMode);
                }
            }
        });
        context.setPacketHandled(true);
    }

    private boolean withinBounds(final Tier tier) {
        if (xRange > tier.XZ_RANGE || zRange > tier.XZ_RANGE || yRange > tier.Y_RANGE || speed > tier.MAX_SPEED || redstoneMode > 3 ||
                xRange < 0 || zRange < 0 || yRange < 0 || speed < 1 || redstoneMode < 0) {
            Torcherino.LOGGER.error("Data received from client is invalid.");
            return false;
        }
        return true;
    }
}
