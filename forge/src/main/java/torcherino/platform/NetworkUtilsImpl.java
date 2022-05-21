package torcherino.platform;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;
import torcherino.config.Config;
import torcherino.network.OpenScreenMessage;
import torcherino.network.S2CTierSyncMessage;
import torcherino.network.ValueUpdateMessage;

import java.util.HashSet;
import java.util.function.Supplier;

public final class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtilsImpl> instance = Suppliers.memoize(NetworkUtilsImpl::new);
    private final HashSet<String> allowedUuids = new HashSet<>();
    public static SimpleChannel torcherinoChannel;

    public static NetworkUtilsImpl getInstance() {
        return instance.get();
    }

    public void initialize() {
        String version = "2";
        torcherinoChannel = NetworkRegistry.newSimpleChannel(Torcherino.getRl("channel"), () -> version, version::equals, version::equals);
        torcherinoChannel.registerMessage(0, ValueUpdateMessage.class, ValueUpdateMessage::encode, ValueUpdateMessage::decode, ValueUpdateMessage::handle);
        torcherinoChannel.registerMessage(1, OpenScreenMessage.class, OpenScreenMessage::encode, OpenScreenMessage::decode, OpenScreenMessage::handle);
        torcherinoChannel.registerMessage(2, S2CTierSyncMessage.class, S2CTierSyncMessage::encode, S2CTierSyncMessage::decode, S2CTierSyncMessage::handle);
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedOut);
    }

    private void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            allowedUuids.add(player.getStringUUID());
            S2CTierSyncMessage message = new S2CTierSyncMessage(TorcherinoAPI.INSTANCE.getTiers());
            torcherinoChannel.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    private void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) {
                allowedUuids.remove(player.getStringUUID());
            }
        }
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        torcherinoChannel.sendToServer(new ValueUpdateMessage(pos, xRange, zRange, yRange, speed, redstoneMode));
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        torcherinoChannel.sendTo(new OpenScreenMessage(pos, name, xRange, zRange, yRange, speed, redstoneMode), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public boolean s_isPlayerOnline(String uuid) {
        return allowedUuids.contains(uuid);
    }
}
