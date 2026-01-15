package torcherino.block.api;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.WeatheringCopperBlocks;

public class LanterinoOxidizableRegistry {

    public static void registerWeatheringSet(WeatheringCopperBlocks blocks) {
        OxidizableBlocksRegistry.registerNextStage(blocks.unaffected(), blocks.exposed());
        OxidizableBlocksRegistry.registerNextStage(blocks.exposed(), blocks.weathered());
        OxidizableBlocksRegistry.registerNextStage(blocks.weathered(), blocks.oxidized());

        OxidizableBlocksRegistry.registerWaxable(blocks.unaffected(), blocks.waxed());
        OxidizableBlocksRegistry.registerWaxable(blocks.exposed(), blocks.waxedExposed());
        OxidizableBlocksRegistry.registerWaxable(blocks.weathered(), blocks.waxedWeathered());
        OxidizableBlocksRegistry.registerWaxable(blocks.oxidized(), blocks.waxedOxidized());
    }
}
