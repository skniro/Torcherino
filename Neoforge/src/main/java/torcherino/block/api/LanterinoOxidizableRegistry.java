package torcherino.block.api;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Objects;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class LanterinoOxidizableRegistry {

    public static void registerWeatheringSet(
            Supplier<? extends Block> base,
            Supplier<? extends Block> exposed,
            Supplier<? extends Block> weathered,
            Supplier<? extends Block> oxidized,
            Supplier<? extends Block> waxedBase,
            Supplier<? extends Block> waxedExposed,
            Supplier<? extends Block> waxedWeathered,
            Supplier<? extends Block> waxedOxidized
    ) {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(() -> {
                registerPair(base.get(), exposed.get());
                registerPair(exposed.get(), weathered.get());
                registerPair(weathered.get(), oxidized.get());

                registerWaxedPair(base.get(), waxedBase);
                registerWaxedPair(exposed.get(), waxedExposed);
                registerWaxedPair(weathered.get(), waxedWeathered);
                registerWaxedPair(oxidized.get(), waxedOxidized);
            });
        });
    }

    private static void registerWaxedPair(Block original, Supplier<? extends Block> waxed) {
        if (waxed != null) {
            BiMap<Block, Block> waxables = (BiMap<Block, Block>) HoneycombItem.WAXABLES.get();
            BiMap<Block, Block> waxOffs = (BiMap<Block, Block>) HoneycombItem.WAX_OFF_BY_BLOCK.get();
            waxables.put(original, waxed.get());
            waxOffs.put(waxed.get(), original);
        }
    }

    public static void registerPair(Block less, Block more) {
        Objects.requireNonNull(less, "Oxidizable block cannot be null!");
        Objects.requireNonNull(more, "Oxidizable block cannot be null!");
        BiMap<Block, Block> nexts = (BiMap<Block, Block>) WeatheringCopper.NEXT_BY_BLOCK.get();
        BiMap<Block, Block> prevs = (BiMap<Block, Block>) WeatheringCopper.PREVIOUS_BY_BLOCK.get();
        nexts.put(less, more);
        prevs.put(more, less);
        refreshRandomTickCache(less);
        refreshRandomTickCache(more);
    }

    private static void refreshRandomTickCache(Block block) {
        block.getStateDefinition().getPossibleStates().forEach(state ->
                ((RandomTickCacheRefresher) state).torcherino$refreshRandomTickCache()
        );
    }

    public interface RandomTickCacheRefresher {
        void torcherino$refreshRandomTickCache();
    }
}
