package torcherino.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import torcherino.Torcherino;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("ClassCanBeRecord")
public record OpenScreenMessage(BlockPos pos, String title,  int xRange, int zRange, int yRange, int speed, int redstoneMode) implements CustomPacketPayload {
    public static final ResourceLocation OPEN_TORCHERINO_SCREEN = Torcherino.resloc("open_torcherino_screen");
    public static final CustomPacketPayload.Type<OpenScreenMessage> TYPE = new CustomPacketPayload.Type<>(OPEN_TORCHERINO_SCREEN);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenMessage> CODEC = CustomPacketPayload.codec(OpenScreenMessage::write, OpenScreenMessage::new);

    public OpenScreenMessage(final FriendlyByteBuf buf){
        this(buf.readBlockPos(),
                buf.readUtf(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeUtf(title);
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
    }



    public static void openTorcherinoScreen(OpenScreenMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class<?> cls = Class.forName("torcherino.network.ClientOpenScreenHandler");
                cls.getMethod("open", OpenScreenMessage.class, IPayloadContext.class).invoke(null, message, context);
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
