package torcherino.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import torcherino.Torcherino;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public record OpenScreenMessage(BlockPos pos, String title,  int xRange, int zRange, int yRange, int speed, int redstoneMode) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_TORCHERINO_SCREEN = Torcherino.resloc("open_torcherino_screen");
    public static final CustomPacketPayload.Type<OpenScreenMessage> TYPE = new CustomPacketPayload.Type<>(OPEN_TORCHERINO_SCREEN);
    public static final StreamCodec<FriendlyByteBuf, OpenScreenMessage> CODEC = CustomPacketPayload.codec(OpenScreenMessage::encode, OpenScreenMessage::decode);


    public static void encode(OpenScreenMessage message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos).writeUtf(message.title).writeInt(message.xRange)
              .writeInt(message.zRange).writeInt(message.yRange).writeInt(message.speed).writeInt(message.redstoneMode);
    }

    public static OpenScreenMessage decode(FriendlyByteBuf buffer) {
        return new OpenScreenMessage(buffer.readBlockPos(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(),
                buffer.readInt());
    }

    public static void handle(OpenScreenMessage message, CustomPayloadEvent.Context contextSupplier) {
        CustomPayloadEvent.Context context = contextSupplier;
            OpenScreenMessage.openTorcherinoScreen(message);
            context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openTorcherinoScreen(OpenScreenMessage message) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.submitAsync(() -> {
            if (minecraft.player.level().getBlockEntity(message.pos) instanceof TorcherinoBlockEntity blockEntity) {
                TorcherinoScreen screen = new TorcherinoScreen(Component.translatable(message.title), message.xRange, message.zRange, message.yRange,
                        message.speed, message.redstoneMode, blockEntity.getBlockPos(), blockEntity.getTier());
                minecraft.setScreen(screen);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
