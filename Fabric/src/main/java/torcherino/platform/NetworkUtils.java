package torcherino.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import torcherino.Torcherino;

public interface NetworkUtils {
    static NetworkUtils getInstance() {
        return Services.NETWORK;
    }

    void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode);

    void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos worldPosition, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode);

    boolean s_isPlayerOnline(String uuid);
}
