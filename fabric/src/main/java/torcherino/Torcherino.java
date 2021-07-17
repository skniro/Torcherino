package torcherino;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.api.entrypoints.TorcherinoInitializer;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.blocks.ModBlocks;
import torcherino.config.Config;
import torcherino.platform.NetworkUtils;
import torcherino.temp.PlayerConnectCallback;
import torcherino.temp.PlayerDisconnectCallback;

import java.util.ArrayList;
import java.util.HashSet;

public class Torcherino implements ModInitializer, TorcherinoInitializer {
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final HashSet<String> allowedUuids = new HashSet<>();
    public static ArrayList<SimpleParticleType> particles = new ArrayList<>();

    public static boolean hasIsOnline(String uuid) {
        return allowedUuids.contains(uuid);
    }

    @Override
    public void onInitialize() {
        Config.initialize();
        TorcherinoAPI.INSTANCE.getTiers().forEach((id, tier) -> {
            if (!id.getNamespace().equals(MOD_ID)) {
                return;
            }
            String path = id.getPath() + "_flame";
            if (path.equals("normal_flame")) {
                path = "flame";
            }
            particles.add(Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, path), new SimpleParticleType(false)));
        });
        ModBlocks.INSTANCE.initialize();
        // todo: replace with networking v1
        ServerSidePacketRegistry.INSTANCE.register(new ResourceLocation(Torcherino.MOD_ID, "utv"), (PacketContext context, FriendlyByteBuf buffer) ->
        {
            Level world = context.getPlayer().getCommandSenderWorld();
            BlockPos pos = buffer.readBlockPos();
            int xRange = buffer.readInt();
            int zRange = buffer.readInt();
            int yRange = buffer.readInt();
            int speed = buffer.readInt();
            int redstoneMode = buffer.readInt();
            context.getTaskQueue().execute(() -> {
                if (world.getBlockEntity(pos) instanceof TorcherinoBlockEntity blockEntity) {
                    if (!blockEntity.readClientData(xRange, zRange, yRange, speed, redstoneMode)) {
                        Torcherino.LOGGER.error("Data received from " + context.getPlayer().getName().getString() + "(" + context.getPlayer().getStringUUID() + ") is invalid.");
                    }
                }
            });
        });
        FabricLoader.getInstance().getEntrypoints("torcherinoInitializer", TorcherinoInitializer.class).forEach(TorcherinoInitializer::onTorcherinoInitialize);
        PlayerConnectCallback.EVENT.register(player ->
        {
            allowedUuids.add(player.getStringUUID());
            NetworkUtils.getInstance().s2c_sendTorcherinoTiers(player);
        });
        PlayerDisconnectCallback.EVENT.register(player ->
        {
            if (Config.INSTANCE.online_mode.equals("ONLINE")) {
                allowedUuids.remove(player.getStringUUID());
            }
        });
    }

    @Override
    public void onTorcherinoInitialize() {
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.WATER);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.LAVA);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.CAVE_AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.VOID_AIR);
        if (FabricLoader.getInstance().isModLoaded("computercraft")) {
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new ResourceLocation("computercraft", "turtle_normal"));
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(new ResourceLocation("computercraft", "turtle_advanced"));
        }
    }
}
