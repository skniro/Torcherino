package torcherino.block.api;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import torcherino.block.CopperLanterinoBlock;
import torcherino.block.WeatheringLanterinoBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LanterinoOxidizableRegistry {

    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> NEXT_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> PREVIOUS_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> WAXABLES_BY_TIER = new HashMap<>();
    private static final Map<ResourceLocation, Supplier<BiMap<Block, Block>>> UNWAXABLES_BY_TIER = new HashMap<>();

    private static final List<Runnable> pendingRegistrations = new ArrayList<>();

    public static void addPendingTier(ResourceLocation tierID,
                                      Supplier<WeatheringLanterinoBlock> copper,
                                      Supplier<WeatheringLanterinoBlock> exposed,
                                      Supplier<WeatheringLanterinoBlock> weathered,
                                      Supplier<WeatheringLanterinoBlock> oxidized,
                                      Supplier<CopperLanterinoBlock> waxed,
                                      Supplier<CopperLanterinoBlock> waxedExposed,
                                      Supplier<CopperLanterinoBlock> waxedWeathered,
                                      Supplier<CopperLanterinoBlock> waxedOxidized) {

        pendingRegistrations.add(() -> registerTier(tierID, copper, exposed, weathered, oxidized, waxed, waxedExposed, waxedWeathered, waxedOxidized));
    }

    public static void init() {
        for (Runnable r : pendingRegistrations) {
            r.run();
        }
        pendingRegistrations.clear();
        //debugPrintAll();
    }

    public static void debugPrintAll() {
        System.out.println("===== LanterinoOxidizableRegistry DEBUG =====");

        for (ResourceLocation tierID : NEXT_BY_TIER.keySet()) {
            System.out.println("Tier: " + tierID);

            BiMap<Block, Block> next = NEXT_BY_TIER.get(tierID).get();
            System.out.println("  NEXT:");
            for (Map.Entry<Block, Block> e : next.entrySet()) {
                System.out.println("    " + e.getKey().toString() + " -> " + e.getValue().toString());
            }

            BiMap<Block, Block> prev = PREVIOUS_BY_TIER.get(tierID).get();
            System.out.println("  PREVIOUS:");
            for (Map.Entry<Block, Block> e : prev.entrySet()) {
                System.out.println("    " + e.getKey().toString() + " -> " + e.getValue().toString());
            }

            BiMap<Block, Block> wax = WAXABLES_BY_TIER.get(tierID).get();
            System.out.println("  WAXABLES:");
            for (Map.Entry<Block, Block> e : wax.entrySet()) {
                System.out.println("    " + e.getKey().toString() + " -> " + e.getValue().toString());
            }

            BiMap<Block, Block> unwax = UNWAXABLES_BY_TIER.get(tierID).get();
            System.out.println("  UNWAXABLES:");
            for (Map.Entry<Block, Block> e : unwax.entrySet()) {
                System.out.println("    " + e.getKey().toString() + " -> " + e.getValue().toString());
            }
        }

        System.out.println("===== END DEBUG =====");
    }

    private static void registerTier(ResourceLocation tierID,
                                     Supplier<WeatheringLanterinoBlock> copper,
                                     Supplier<WeatheringLanterinoBlock> exposed,
                                     Supplier<WeatheringLanterinoBlock> weathered,
                                     Supplier<WeatheringLanterinoBlock> oxidized,
                                     Supplier<CopperLanterinoBlock> waxed,
                                     Supplier<CopperLanterinoBlock> waxedExposed,
                                     Supplier<CopperLanterinoBlock> waxedWeathered,
                                     Supplier<CopperLanterinoBlock> waxedOxidized) {

        Supplier<BiMap<Block, Block>> nextMap = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
                                                                                      .put(copper.get(), exposed.get())
                                                                                      .put(exposed.get(), weathered.get())
                                                                                      .put(weathered.get(), oxidized.get())
                                                                                      .build()
        );
        NEXT_BY_TIER.put(tierID, nextMap);
        PREVIOUS_BY_TIER.put(tierID, Suppliers.memoize(() -> nextMap.get().inverse()));

        Supplier<BiMap<Block, Block>> waxMap = Suppliers.memoize(() -> ImmutableBiMap.<Block, Block>builder()
                                                                                     .put(copper.get(), waxed.get())
                                                                                     .put(exposed.get(), waxedExposed.get())
                                                                                     .put(weathered.get(), waxedWeathered.get())
                                                                                     .put(oxidized.get(), waxedOxidized.get())
                                                                                     .build()
        );
        WAXABLES_BY_TIER.put(tierID, waxMap);
        UNWAXABLES_BY_TIER.put(tierID, Suppliers.memoize(() -> waxMap.get().inverse()));
    }

    public static BiMap<Block, Block> getNextByBlock(ResourceLocation tierID) {
        return NEXT_BY_TIER.getOrDefault(tierID, Suppliers.memoize(ImmutableBiMap::of)).get();
    }

    public static BiMap<Block, Block> getPreviousByBlock(ResourceLocation tierID) {
        return PREVIOUS_BY_TIER.getOrDefault(tierID, Suppliers.memoize(ImmutableBiMap::of)).get();
    }

    public static BiMap<Block, Block> getWaxMap(ResourceLocation tierID) {
        return WAXABLES_BY_TIER.getOrDefault(tierID, Suppliers.memoize(ImmutableBiMap::of)).get();
    }

    public static BiMap<Block, Block> getUnwaxMap(ResourceLocation tierID) {
        return UNWAXABLES_BY_TIER.getOrDefault(tierID, Suppliers.memoize(ImmutableBiMap::of)).get();
    }
}
