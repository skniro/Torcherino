package torcherino_unofficial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import torcherino.ClientHelper;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;
import torcherino_unofficial.block.CopperLanterinoBlock;
import torcherino_unofficial.block.WeatheringLanterinoBlock;
import torcherino_unofficial.block.api.LanterinoDegradable;
import torcherino_unofficial.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@EventBusSubscriber(modid = TorcherinoUnofficial.MOD_ID)
public final class UnofficialContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Torcherino.MOD_ID);
    private static final Set<Identifier> TO_BLACKLIST = new HashSet<>();
    private static final List<Supplier<? extends Block>> COPPER_BLOCKS = new ArrayList<>();

    public static void initialise(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILE_ENTITIES.register(bus);
        TorcherinoAPI.INSTANCE.getTiers().keySet().forEach(UnofficialContent::register);

        TILE_ENTITIES.register("copper_lantern", () -> new BlockEntityType<>(
                UnofficialContent::newBlockEntity,
                COPPER_BLOCKS.stream().map(Supplier::get).toArray(Block[]::new)
        ));
    }

    private static TorcherinoBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TorcherinoBlockEntity(CopperLanterinoBlock.copperBlockEntityType(), pos, state);
    }

    private static void register(Identifier tierID) {
        if (!tierID.getNamespace().equals(Torcherino.MOD_ID)) {
            return;
        }
        String copperLanterinoId = getPath(tierID, "copper_lantern");
        String exposedCopperId = "exposed_" + copperLanterinoId;
        String weatheredCopperId = "weathered_" + copperLanterinoId;
        String oxidizedCopperId = "oxidized_" + copperLanterinoId;
        String waxedCopperId = "waxed_" + copperLanterinoId;
        String waxedExposedCopperId = "waxed_exposed_" + copperLanterinoId;
        String waxedWeatheredCopperId = "waxed_weathered_" + copperLanterinoId;
        String waxedOxidizedCopperId = "waxed_oxidized_" + copperLanterinoId;

        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, copperLanterinoId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, exposedCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, weatheredCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, oxidizedCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, waxedCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, waxedExposedCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, waxedWeatheredCopperId));
        TO_BLACKLIST.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, waxedOxidizedCopperId));

        Supplier<WeatheringLanterinoBlock> copperLantern = registerBlock(copperLanterinoId,
                () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.UNAFFECTED, copperProperties(copperLanterinoId), tierID));
        Supplier<WeatheringLanterinoBlock> exposedCopper = registerBlock(exposedCopperId,
                () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.EXPOSED, copperProperties(exposedCopperId), tierID));
        Supplier<WeatheringLanterinoBlock> weatheredCopper = registerBlock(weatheredCopperId,
                () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.WEATHERED, copperProperties(weatheredCopperId), tierID));
        Supplier<WeatheringLanterinoBlock> oxidizedCopper = registerBlock(oxidizedCopperId,
                () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.OXIDIZED, copperProperties(oxidizedCopperId), tierID));
        Supplier<CopperLanterinoBlock> waxedCopper = registerBlock(waxedCopperId,
                () -> new CopperLanterinoBlock(copperProperties(waxedCopperId), tierID));
        Supplier<CopperLanterinoBlock> waxedExposedCopper = registerBlock(waxedExposedCopperId,
                () -> new CopperLanterinoBlock(copperProperties(waxedExposedCopperId), tierID));
        Supplier<CopperLanterinoBlock> waxedWeatheredCopper = registerBlock(waxedWeatheredCopperId,
                () -> new CopperLanterinoBlock(copperProperties(waxedWeatheredCopperId), tierID));
        Supplier<CopperLanterinoBlock> waxedOxidizedCopper = registerBlock(waxedOxidizedCopperId,
                () -> new CopperLanterinoBlock(copperProperties(waxedOxidizedCopperId), tierID));

        registerItem(copperLanterinoId, copperLantern);
        registerItem(exposedCopperId, exposedCopper);
        registerItem(weatheredCopperId, weatheredCopper);
        registerItem(oxidizedCopperId, oxidizedCopper);
        registerItem(waxedCopperId, waxedCopper);
        registerItem(waxedExposedCopperId, waxedExposedCopper);
        registerItem(waxedWeatheredCopperId, waxedWeatheredCopper);
        registerItem(waxedOxidizedCopperId, waxedOxidizedCopper);

        LanterinoOxidizableRegistry.addPendingTier(
                tierID,
                copperLantern, exposedCopper, weatheredCopper, oxidizedCopper,
                waxedCopper, waxedExposedCopper, waxedWeatheredCopper, waxedOxidizedCopper
        );

        if (FMLEnvironment.getDist().isClient()) {
            ClientHelper.registerCutout(copperLantern);
            ClientHelper.registerCutout(exposedCopper);
            ClientHelper.registerCutout(weatheredCopper);
            ClientHelper.registerCutout(oxidizedCopper);
            ClientHelper.registerCutout(waxedCopper);
            ClientHelper.registerCutout(waxedExposedCopper);
            ClientHelper.registerCutout(waxedWeatheredCopper);
            ClientHelper.registerCutout(waxedOxidizedCopper);
        }
    }

    private static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> supplier) {
        Supplier<T> registered = BLOCKS.register(id, supplier);
        COPPER_BLOCKS.add(registered);
        return registered;
    }

    private static void registerItem(String id, Supplier<? extends Block> block) {
        ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(Torcherino.KeyofItem(id))));
    }

    private static BlockBehaviour.Properties copperProperties(String id) {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .forceSolidOn()
                .strength(3.5F)
                .sound(SoundType.LANTERN)
                .lightLevel(state -> 15)
                .noOcclusion()
                .pushReaction(PushReaction.IGNORE_ENTITY)
                .randomTicks()
                .setId(Torcherino.KeyofBlock(id));
    }

    private static String getPath(Identifier tierID, String type) {
        return (tierID.getPath().equals("normal") ? "" : tierID.getPath() + "_") + type;
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (Identifier id : TO_BLACKLIST) {
                TorcherinoAPI.INSTANCE.blacklistBlock(id);
            }
            LanterinoOxidizableRegistry.init();
        });
    }

    @SubscribeEvent
    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            ITEMS.getEntries().stream().map(Supplier::get).forEach(event::accept);
        }
    }

    private UnofficialContent() {
    }
}
