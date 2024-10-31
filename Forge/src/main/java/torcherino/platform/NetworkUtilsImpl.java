package torcherino.platform;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
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
        torcherinoChannel = NetworkRegistry.ChannelBuilder
                .named(Torcherino.getRl("channel")) // 传入资源定位符 (ResourceLocation)
                .networkProtocolVersion(() -> version) // 定义协议版本
                .clientAcceptedVersions(version::equals) // 客户端协议版本验证
                .serverAcceptedVersions(version::equals) // 服务器协议版本验证
                .simpleChannel();
        torcherinoChannel.messageBuilder(ValueUpdateMessage.class, 0)
                         .encoder(ValueUpdateMessage::encode)
                         .decoder(ValueUpdateMessage::decode)
                         .consumerMainThread(ValueUpdateMessage::handle)
                         .add();
        torcherinoChannel.messageBuilder(OpenScreenMessage.class, 1)
                         .encoder(OpenScreenMessage::encode)
                         .decoder(OpenScreenMessage::decode)
                         .consumerMainThread(OpenScreenMessage::handle)
                         .add();
        torcherinoChannel.messageBuilder(S2CTierSyncMessage.class, 2)
                         .encoder(S2CTierSyncMessage::encode)
                         .decoder(S2CTierSyncMessage::decode)
                         .consumerMainThread(S2CTierSyncMessage::handle)
                         .add();
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::playerLoggedOut);
    }

    private void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            allowedUuids.add(player.getStringUUID());
            S2CTierSyncMessage message = new S2CTierSyncMessage(TorcherinoAPI.INSTANCE.getTiers());
            torcherinoChannel.send(message, player.connection.getConnection());
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
        ValueUpdateMessage message = new ValueUpdateMessage(pos, xRange, zRange, yRange, speed, redstoneMode);
        torcherinoChannel.send(message, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        torcherinoChannel.send(new OpenScreenMessage(pos, name, xRange, zRange, yRange, speed, redstoneMode), player.connection.getConnection());
    }

    @Override
    public boolean s_isPlayerOnline(String uuid) {
        return allowedUuids.contains(uuid);
    }
}
