package torcherino;


import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import torcherino.particle.TorcherinoParticleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientHelper {
    private static final List<Supplier<? extends Block>> CUTOUT_BLOCKS = new ArrayList<>();

    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TorcherinoParticleTypes.Normal_Torcherino_Flame.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(TorcherinoParticleTypes.Compressed_Torcherino_Flame.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(TorcherinoParticleTypes.Double_Compressed_Torcherino_Flame.get(), FlameParticle.Provider::new);
    }

    public static void registerCutout(Supplier<? extends Block> block) {
        CUTOUT_BLOCKS.add(block);
    }

    // Render layers can only be set once the client is running; mod construction also
    // happens on the data generator where Minecraft.getInstance() is null.
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> CUTOUT_BLOCKS.forEach(block ->
                ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout())));
    }
}
