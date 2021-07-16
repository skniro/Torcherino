package torcherino.platform;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import torcherino.network.Networker;
import torcherino.network.ValueUpdateMessage;

import java.util.function.Supplier;

public class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtils> instance = Suppliers.memoize(NetworkUtilsImpl::new);

    public static NetworkUtils getInstance() {
        return instance.get();
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        Networker.INSTANCE.torcherinoChannel.sendToServer(new ValueUpdateMessage(pos, xRange, zRange, yRange, speed, redstoneMode));
    }
}
