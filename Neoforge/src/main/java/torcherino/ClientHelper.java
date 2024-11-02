package torcherino;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;


import java.util.function.Supplier;

@EventBusSubscriber(modid = Torcherino.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHelper {
    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        ModContent.PARTICLE_TYPES.getEntries().forEach(registryObject -> Minecraft.getInstance().particleEngine.register((SimpleParticleType) registryObject.get(), FlameParticle.Provider::new));
    }

    public static void registerCutout(Supplier<? extends Block> block){
        Minecraft.getInstance().submitAsync(() -> {
            ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        });
    }
}
