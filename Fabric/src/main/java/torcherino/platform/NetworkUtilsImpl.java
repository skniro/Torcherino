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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import torcherino.Torcherino;
import torcherino.TorcherinoImpl;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.screen.TorcherinoScreen;
import torcherino.config.Config;
import torcherino.platform.payload.OpenTorchrinoScreenPayload;
import torcherino.platform.payload.TorchrinoTierPayload;
import torcherino.platform.payload.UpdateTorchrinoPayload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

public class NetworkUtilsImpl implements NetworkUtils {
    private static final Supplier<NetworkUtilsImpl> instance = Suppliers.memoize(NetworkUtilsImpl::new);

    private final HashSet<String> allowedUuids = new HashSet<>();

    public static NetworkUtilsImpl getInstance() {
        return instance.get();
    }

    @Override
    public void c2s_updateTorcherinoValues(BlockPos pos, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        if (ClientPlayNetworking.canSend(UpdateTorchrinoPayload.TYPE)) {
            ClientPlayNetworking.send(new UpdateTorchrinoPayload(pos, xRange, zRange, yRange, speed, redstoneMode));
        }
    }

    @Override
    public void s2c_openTorcherinoScreen(ServerPlayer player, BlockPos pos, String name, int xRange, int zRange, int yRange, int speed, int redstoneMode) {
        if (ClientPlayNetworking.canSend(OpenTorchrinoScreenPayload.TYPE)) {
            NetworkManager.sendToPlayer(new OpenTorchrinoScreenPayload(pos, name, xRange, zRange, yRange, speed, redstoneMode), player);
        }
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
            sender.sendPacket(new TorchrinoTierPayload(buffer));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) {
                allowedUuids.remove(handler.player.getStringUUID());
            }
        });

        ServerPlayConnectionEvents.INIT.register((handler, server) -> {
            ServerPlayNetworking.registerReceiver(handler, UpdateTorchrinoPayload.TYPE, (payload, context) -> {
                Level level = context.player().level();
                context.server().execute(() -> {
                    if (level.getBlockEntity(payload.blockPos()) instanceof TorcherinoBlockEntity blockEntity) {
                        if(!blockEntity.readClientData(payload.xRange(), payload.zRange(), payload.yRange(), payload.speed(), payload.redstoneMode())) {
                            Torcherino.LOGGER.error("Data received from " + context.player().getName().getString() + "(" + context.player().getStringUUID() + ") is invalid.");
                        }
                    }
                });
            });
        });
    }

    @Override
    public boolean s_isPlayerOnline(String uuid) {
        return allowedUuids.contains(uuid);
    }

    private static class ClientNetworking {
        private static void initialize() {
            ClientPlayConnectionEvents.INIT.register((handler, client) -> {
                ClientPlayNetworking.registerReceiver(OpenTorchrinoScreenPayload.TYPE, (payload, context) -> {
                    Level world = Minecraft.getInstance().level;
                    context.client().execute(() -> {
                        if (world.getBlockEntity(payload.blockPos()) instanceof TorcherinoBlockEntity blockEntity) {
                            Minecraft.getInstance().setScreen(new TorcherinoScreen(payload.title(), payload.xRange(), payload.zRange(), payload.yRange(), payload.speed(), payload.redstoneMode(), payload.blockPos(), blockEntity.getTier()));
                        }
                    });
                });

                ClientPlayNetworking.registerReceiver(TorchrinoTierPayload.TYPE, (payload, context) -> {
                    HashMap<ResourceLocation, Tier> tiers = new HashMap<>();
                    tiers.put(payload.resourceLocation(), new Tier(payload.maxSpeed(), payload.xzRange(), payload.yRange()));
                    context.client().execute(() -> ((TorcherinoImpl) TorcherinoAPI.INSTANCE).setRemoteTiers(tiers));
                });
            });
        }
    }
}
