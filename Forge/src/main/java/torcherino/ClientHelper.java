package torcherino;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import torcherino.particle.TorcherinoParticleTypes;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
