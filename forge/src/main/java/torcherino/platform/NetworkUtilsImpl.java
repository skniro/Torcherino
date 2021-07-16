package torcherino.platform;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;
import torcherino.network.OpenScreenMessage;
import torcherino.network.S2CTierSyncMessage;
import torcherino.network.ValueUpdateMessage;

import java.util.function.Supplier;

public final class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtilsImpl> instance = Suppliers.memoize(NetworkUtilsImpl::new);
    public SimpleChannel torcherinoChannel;

    public static NetworkUtilsImpl getInstance() {
        return instance.get();
    }

    public void initialize() {
        final String version = "2";
        torcherinoChannel = NetworkRegistry.newSimpleChannel(Torcherino.getRl("channel"), () -> version, version::equals, version::equals);
        torcherinoChannel.registerMessage(0, ValueUpdateMessage.class, ValueUpdateMessage::encode, ValueUpdateMessage::decode, ValueUpdateMessage::handle);
        torcherinoChannel.registerMessage(1, OpenScreenMessage.class, OpenScreenMessage::encode, OpenScreenMessage::decode, OpenScreenMessage::handle);
        torcherinoChannel.registerMessage(2, S2CTierSyncMessage.class, S2CTierSyncMessage::encode, S2CTierSyncMessage::decode, S2CTierSyncMessage::handle);
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        torcherinoChannel.sendToServer(new ValueUpdateMessage(pos, xRange, zRange, yRange, speed, redstoneMode));
    }

    @Override
    public void s2c_sendTorcherinoTiers(ServerPlayer player) {
        final S2CTierSyncMessage message = new S2CTierSyncMessage(TorcherinoAPI.INSTANCE.getTiers());
        torcherinoChannel.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        torcherinoChannel.sendTo(new OpenScreenMessage(pos, name, xRange, zRange, yRange, speed, redstoneMode), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
