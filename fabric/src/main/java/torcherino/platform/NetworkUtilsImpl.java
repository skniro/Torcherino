package torcherino.platform;

import com.google.common.base.Suppliers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import torcherino.Torcherino;

import java.util.function.Supplier;

public class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtils> instance = Suppliers.memoize(NetworkUtilsImpl::new);

    public static NetworkUtils getInstance() {
        return instance.get();
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeBlockPos(pos);
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
        // todo: replace with networking v1
        ClientSidePacketRegistry.INSTANCE.sendToServer(new ResourceLocation(Torcherino.MOD_ID, "utv"), buffer);
    }
}
