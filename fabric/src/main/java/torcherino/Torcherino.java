package torcherino;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.api.entrypoints.TorcherinoInitializer;
import torcherino.blocks.ModBlocks;
import torcherino.config.Config;
import torcherino.platform.NetworkUtilsImpl;

public final class Torcherino implements ModInitializer, TorcherinoInitializer {
    public static final String MOD_ID = "torcherino";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(Torcherino.MOD_ID, path);
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
            ParticleFactoryRegistry.getInstance().register(Registry.register(Registry.PARTICLE_TYPE, Torcherino.resloc(path), new SimpleParticleType(false)),
                    FlameParticle.Provider::new);
        });
        ModBlocks.INSTANCE.initialize();
        FabricLoader.getInstance().getEntrypoints("torcherinoInitializer", TorcherinoInitializer.class).forEach(TorcherinoInitializer::onTorcherinoInitialize);
        NetworkUtilsImpl.getInstance().initialize();
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
