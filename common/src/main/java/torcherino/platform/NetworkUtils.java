package torcherino.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public interface NetworkUtils {
    static NetworkUtils getInstance() {
        return NetworkUtilsImpl.getInstance();
    }

    void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode);

    void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos worldPosition, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode);

    boolean s_isPlayerOnline(String uuid);
}
