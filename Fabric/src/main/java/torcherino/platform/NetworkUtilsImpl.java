package torcherino.platform;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import torcherino.Torcherino;
import torcherino.TorcherinoImpl;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;
import torcherino.config.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

public class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtilsImpl> instance = Suppliers.memoize(NetworkUtilsImpl::new);
    private static final ResourceLocation UPDATE_TORCHERINO_VALUES = Torcherino.resloc("update_torcherino_values");
    private static final ResourceLocation TORCHERINO_TIER_SYNC = Torcherino.resloc("torcherino_tier_sync");
    private static final ResourceLocation OPEN_TORCHERINO_SCREEN = Torcherino.resloc("open_torcherino_screen");
    private final HashSet<String> allowedUuids = new HashSet<>();

    public static NetworkUtilsImpl getInstance() {
        return instance.get();
    }

    public void initialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientNetworking.initialize();
        }
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            allowedUuids.add(player.getStringUUID());

            ImmutableMap<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeInt(tiers.size());
            tiers.forEach((id, tier) -> {
                buffer.writeResourceLocation(id);
                buffer.writeInt(tier.maxSpeed());
                buffer.writeInt(tier.xzRange());
                buffer.writeInt(tier.yRange());
            });

            sender.sendPacket(TORCHERINO_TIER_SYNC, buffer);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) {
                allowedUuids.remove(handler.player.getStringUUID());
            }
        });
        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, NetworkUtilsImpl.UPDATE_TORCHERINO_VALUES, (server1, player, handler1, buffer, responseSender) -> {
                Level level = player.level();
                BlockPos pos = buffer.readBlockPos();
                int xRange = buffer.readInt();
                int zRange = buffer.readInt();
                int yRange = buffer.readInt();
                int speed = buffer.readInt();
                int redstoneMode = buffer.readInt();
                server1.submit(() -> {
                    if (level.getBlockEntity(pos) instanceof TorcherinoBlockEntity blockEntity) {
                        if (!blockEntity.readClientData(xRange, zRange, yRange, speed, redstoneMode)) {
                            Torcherino.LOGGER.error("Data received from " + player.getName().getString() + "(" + player.getStringUUID() + ") is invalid.");
                        }
                    }
                });
            });
        });
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        if (ClientPlayNetworking.canSend(NetworkUtilsImpl.UPDATE_TORCHERINO_VALUES)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeBlockPos(pos);
            buffer.writeInt(xRange);
            buffer.writeInt(zRange);
            buffer.writeInt(yRange);
            buffer.writeInt(speed);
            buffer.writeInt(redstoneMode);
            ClientPlayNetworking.send(NetworkUtilsImpl.UPDATE_TORCHERINO_VALUES, buffer);
        }
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, Component name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        if (ServerPlayNetworking.canSend(player, NetworkUtilsImpl.OPEN_TORCHERINO_SCREEN)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeBlockPos(pos);
            buffer.writeComponent(name);
            buffer.writeInt(xRange);
            buffer.writeInt(zRange);
            buffer.writeInt(yRange);
            buffer.writeInt(speed);
            buffer.writeInt(redstoneMode);
            ServerPlayNetworking.send(player, NetworkUtilsImpl.OPEN_TORCHERINO_SCREEN, buffer);
        }
    }

    @Override
    public boolean s_isPlayerOnline(String uuid) {
        return allowedUuids.contains(uuid);
    }

    private static class ClientNetworking {
        private static void initialize() {
            ClientPlayConnectionEvents.INIT.register((handler, client) -> {
                ClientPlayNetworking.registerReceiver(NetworkUtilsImpl.OPEN_TORCHERINO_SCREEN, (client1, handler1, buffer, responseSender) -> {
                    Level world = Minecraft.getInstance().level;
                    BlockPos pos = buffer.readBlockPos();
                    Component title = buffer.readComponent();
                    int xRange = buffer.readInt();
                    int zRange = buffer.readInt();
                    int yRange = buffer.readInt();
                    int speed = buffer.readInt();
                    int redstoneMode = buffer.readInt();
                    buffer.retain();
                    client1.execute(() -> {
                        if (world.getBlockEntity(pos) instanceof TorcherinoBlockEntity blockEntity) {
                            Minecraft.getInstance().setScreen(new TorcherinoScreen(title, xRange, zRange, yRange, speed, redstoneMode, pos, blockEntity.getTier()));
                        }
                        buffer.release();
                    });
                });

                ClientPlayNetworking.registerReceiver(NetworkUtilsImpl.TORCHERINO_TIER_SYNC, (client1, handler1, buffer, responseSender) -> {
                    HashMap<ResourceLocation, Tier> tiers = new HashMap<>();
                    int count = buffer.readInt();
                    for (int i = 0; i < count; i++) {
                        ResourceLocation id = buffer.readResourceLocation();
                        int maxSpeed = buffer.readInt();
                        int xzRange = buffer.readInt();
                        int yRange = buffer.readInt();
                        tiers.put(id, new Tier(maxSpeed, xzRange, yRange));
                    }
                    client1.execute(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(tiers));
                });
            });
        }
    }
}
