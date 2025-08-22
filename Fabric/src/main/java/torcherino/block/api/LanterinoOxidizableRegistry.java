package torcherino.block.api;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.WeatheringCopperBlocks;

public class LanterinoOxidizableRegistry {

    public static void registerWeatheringSet(WeatheringCopperBlocks blocks) {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(blocks.unaffected(), blocks.exposed());
        OxidizableBlocksRegistry.registerOxidizableBlockPair(blocks.exposed(), blocks.weathered());
        OxidizableBlocksRegistry.registerOxidizableBlockPair(blocks.weathered(), blocks.oxidized());

        OxidizableBlocksRegistry.registerWaxableBlockPair(blocks.unaffected(), blocks.waxed());
        OxidizableBlocksRegistry.registerWaxableBlockPair(blocks.exposed(), blocks.waxedExposed());
        OxidizableBlocksRegistry.registerWaxableBlockPair(blocks.weathered(), blocks.waxedWeathered());
        OxidizableBlocksRegistry.registerWaxableBlockPair(blocks.oxidized(), blocks.waxedOxidized());
    }
}
