package torcherino.platform;

import net.minecraft.core.BlockPos;

public interface NetworkUtils {
    static NetworkUtils getInstance() {
        return NetworkUtilsImpl.getInstance();
    }

    void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode);
}
