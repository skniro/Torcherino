package torcherino.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;

import java.util.function.Supplier;

public final class OpenScreenMessage {
    public final BlockPos pos;
    public final Component title;
    public final int xRange, zRange, yRange, speed, redstoneMode;

    public OpenScreenMessage(final BlockPos pos, final Component title, final int xRange, final int zRange, final int yRange, final int speed,
                             final int redstoneMode) {
        this.pos = pos;
        this.title = title;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed;
        this.redstoneMode = redstoneMode;
    }

    static void encode(final OpenScreenMessage message, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos).writeComponent(message.title).writeInt(message.xRange)
              .writeInt(message.zRange).writeInt(message.yRange).writeInt(message.speed).writeInt(message.redstoneMode);
    }

    static OpenScreenMessage decode(final FriendlyByteBuf buffer) {
        return new OpenScreenMessage(buffer.readBlockPos(), buffer.readComponent(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(),
                buffer.readInt());
    }

    static void handle(final OpenScreenMessage message, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getOriginationSide() == LogicalSide.SERVER) {
            final Minecraft minecraft = Minecraft.getInstance();
            minecraft.submitAsync(() ->
            {
                final BlockEntity tileEntity = minecraft.player.level.getBlockEntity(message.pos);
                if (tileEntity instanceof TorcherinoBlockEntity blockEntity)
                {
                    final TorcherinoScreen screen = new TorcherinoScreen(message.title, message.xRange, message.zRange, message.yRange,
                            message.speed, message.redstoneMode, blockEntity.getBlockPos(), blockEntity.getTierName());
                    minecraft.setScreen(screen);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
