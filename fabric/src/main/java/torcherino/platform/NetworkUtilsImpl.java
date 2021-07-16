package torcherino.platform;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

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

    @Override
    public void s2c_sendTorcherinoTiers(ServerPlayer player) {
        ImmutableMap<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
        packetBuffer.writeInt(tiers.size());
        tiers.forEach((id, tier) -> {
            packetBuffer.writeResourceLocation(id);
            packetBuffer.writeInt(tier.getMaxSpeed());
            packetBuffer.writeInt(tier.getXZRange());
            packetBuffer.writeInt(tier.getYRange());
        });
        // todo: replace with networking v1
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ResourceLocation(Torcherino.MOD_ID, "tts"), packetBuffer);
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeBlockPos(pos);
        buffer.writeComponent(name);
        buffer.writeInt(xRange);
        buffer.writeInt(zRange);
        buffer.writeInt(yRange);
        buffer.writeInt(speed);
        buffer.writeInt(redstoneMode);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new ResourceLocation(Torcherino.MOD_ID, "ots"), buffer);
    }
}
