package torcherino;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import torcherino.particle.TorcherinoParticleTypes;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Torcherino.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHelper {
    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TorcherinoParticleTypes.Normal_Torcherino_Flame.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(TorcherinoParticleTypes.Compressed_Torcherino_Flame.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(TorcherinoParticleTypes.Double_Compressed_Torcherino_Flame.get(), FlameParticle.Provider::new);
    }

    public static void registerCutout(Supplier<? extends Block> block){
        Minecraft.getInstance().submitAsync(() -> {
            ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        });
    }
}
