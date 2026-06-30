package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import java.lang.reflect.InvocationTargetException;
import torcherino.Torcherino;

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

    public static void handle(OpenScreenMessage message, CustomPayloadEvent.Context context) {
            OpenScreenMessage.openTorcherinoScreen(message, context);
            context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openTorcherinoScreen(OpenScreenMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            try {
                Class<?> cls = Class.forName("torcherino.network.ClientOpenScreenHandler");
                cls.getMethod("open", OpenScreenMessage.class, CustomPayloadEvent.Context.class).invoke(null, message, context);
            } catch (ClassNotFoundException ignored) {
                // Dedicated servers must not resolve client-only screen classes.
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Torcherino.LOGGER.error("Failed to invoke ClientOpenScreenHandler", e);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
