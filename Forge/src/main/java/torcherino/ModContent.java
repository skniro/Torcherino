package torcherino;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import torcherino.api.TorcherinoAPI;
import torcherino.block.CopperLanterinoBlock;
import torcherino.block.ForgeTorcherinoBlock;
import torcherino.block.ForgeWallTorcherinoBlock;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WeatheringLanterinoBlock;
import torcherino.block.api.LanterinoDegradable;
import torcherino.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.particle.TorcherinoParticleTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Torcherino.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Torcherino.MOD_ID);

    public static void initialise(BusGroup bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TorcherinoParticleTypes.PARTICLE_TYPES.register(bus);
        TILE_ENTITIES.register(bus);


        TILE_ENTITIES.register("torcherino", () -> new BlockEntityType<TorcherinoBlockEntity>(TorcherinoBlockEntity::new, Set.of(BLOCKS.getEntries().stream().map(Supplier::get).toList().toArray(new Block[0]))));
        toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "torcherino"));
        TorcherinoAPI.INSTANCE.getTiers().keySet().forEach(ModContent::register);
    }

    private static String getPath(ResourceLocation tierID, String type) {
        return (tierID.getPath().equals("normal") ? "" : tierID.getPath() + "_") + type;
    }


    static Supplier<TorcherinoBlock> b;
    private static void register(ResourceLocation tierID) {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID)) {
            String torcherinoPath = getPath(tierID, "torcherino");
            String jackoLanterinoPath = getPath(tierID, "lanterino");
            String lanterinoPath = getPath(tierID, "lantern");
            String copperLanterinoId = getPath(tierID, "copper_lantern");
            String exposedCopperId = "exposed_" + copperLanterinoId;
            String weatheredCopperId = "weathered_" + copperLanterinoId;
            String oxidizedCopperId = "oxidized_" + copperLanterinoId;
            String waxedCopperId = "waxed_" + copperLanterinoId;
            String waxedexposedCopperId = "waxed_exposed_" + copperLanterinoId;
            String waxedweatheredCopperId = "waxed_weathered_" + copperLanterinoId;
            String waxedoxidizedCopperId = "waxed_oxidized_" + copperLanterinoId;

            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, torcherinoPath));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, "wall_" + torcherinoPath));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, jackoLanterinoPath));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, lanterinoPath));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, copperLanterinoId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, exposedCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, weatheredCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, oxidizedCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, waxedCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, waxedexposedCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, waxedweatheredCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, waxedoxidizedCopperId));
            toBlacklist.add(ResourceLocation.fromNamespaceAndPath(Torcherino.MOD_ID, copperLanterinoId));

            Supplier<ForgeTorcherinoBlock> standingBlock = BLOCKS.register(torcherinoPath, () -> new ForgeTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock(torcherinoPath)), tierID));
            Supplier<ForgeWallTorcherinoBlock> wallBlock = BLOCKS.register("wall_" + torcherinoPath, () -> new ForgeWallTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock("wall_" + torcherinoPath)).overrideDescription(standingBlock.get().getDescriptionId()).overrideLootTable(standingBlock.get().getLootTable()), tierID));
            Supplier<JackoLanterinoBlock> jackoLanterinoBlock = BLOCKS.register(jackoLanterinoPath, () -> new JackoLanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock(jackoLanterinoPath)), tierID));
            Supplier<LanterinoBlock> lanterinoBlock = BLOCKS.register(lanterinoPath, () -> new LanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock(lanterinoPath)), tierID));

            Supplier<WeatheringLanterinoBlock> copperLantern = BLOCKS.register(copperLanterinoId, () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(copperLanterinoId)), tierID));
            Supplier<WeatheringLanterinoBlock> exposedCopper = BLOCKS.register(exposedCopperId, () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(exposedCopperId)), tierID));
            Supplier<WeatheringLanterinoBlock> weatheredCopper = BLOCKS.register(weatheredCopperId, () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(weatheredCopperId)), tierID));
            Supplier<WeatheringLanterinoBlock> oxidizedCopper = BLOCKS.register(oxidizedCopperId, () -> new WeatheringLanterinoBlock(LanterinoDegradable.DegradationLevel.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(oxidizedCopperId)), tierID));
            Supplier<CopperLanterinoBlock> waxedCopper = BLOCKS.register(waxedCopperId, () -> new CopperLanterinoBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(waxedCopperId)), tierID));
            Supplier<CopperLanterinoBlock> waxedExposedCopper = BLOCKS.register(waxedexposedCopperId, () -> new CopperLanterinoBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(waxedexposedCopperId)), tierID));
            Supplier<CopperLanterinoBlock> waxedWeatheredCopper = BLOCKS.register(waxedweatheredCopperId, () -> new CopperLanterinoBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(waxedweatheredCopperId)), tierID));
            Supplier<CopperLanterinoBlock> waxedOxidizedCopper = BLOCKS.register(waxedoxidizedCopperId, () -> new CopperLanterinoBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE).randomTicks().setId(Torcherino.KeyofBlock(waxedoxidizedCopperId)), tierID));

            ITEMS.register(torcherinoPath, () -> new StandingAndWallBlockItem(standingBlock.get(), wallBlock.get(), Direction.DOWN, new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(torcherinoPath))));
            ITEMS.register(jackoLanterinoPath, () -> new BlockItem(jackoLanterinoBlock.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(jackoLanterinoPath))));
            ITEMS.register(lanterinoPath, () -> new BlockItem(lanterinoBlock.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(lanterinoPath))));
            ITEMS.register(copperLanterinoId, () -> new BlockItem(copperLantern.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(copperLanterinoId))));
            ITEMS.register(exposedCopperId, () -> new BlockItem(exposedCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(exposedCopperId))));
            ITEMS.register(weatheredCopperId, () -> new BlockItem(weatheredCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(weatheredCopperId))));
            ITEMS.register(oxidizedCopperId, () -> new BlockItem(oxidizedCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(oxidizedCopperId))));
            ITEMS.register(waxedCopperId, () -> new BlockItem(waxedCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(waxedCopperId))));
            ITEMS.register(waxedexposedCopperId, () -> new BlockItem(waxedExposedCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(waxedexposedCopperId))));
            ITEMS.register(waxedweatheredCopperId, () -> new BlockItem(waxedWeatheredCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(waxedweatheredCopperId))));
            ITEMS.register(waxedoxidizedCopperId, () -> new BlockItem(waxedOxidizedCopper.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(waxedoxidizedCopperId))));


            LanterinoOxidizableRegistry.addPendingTier(
                    tierID,
                    copperLantern, exposedCopper, weatheredCopper, oxidizedCopper,
                    waxedCopper, waxedExposedCopper, waxedWeatheredCopper, waxedOxidizedCopper
            );

            if (FMLLoader.getDist().isClient()) {
                ClientHelper.registerCutout(standingBlock);
                ClientHelper.registerCutout(wallBlock);
                ClientHelper.registerCutout(lanterinoBlock);
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
    }

    private static final Set<ResourceLocation> toBlacklist = new HashSet<>();

    @SubscribeEvent
    public static void blackliststuff(final FMLCommonSetupEvent event) {
        for (ResourceLocation block : toBlacklist){
            TorcherinoAPI.INSTANCE.blacklistBlock(block);
        }
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.WATER);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.LAVA);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.CAVE_AIR);
        TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.VOID_AIR);
    }

    @SubscribeEvent
    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){
            ITEMS.getEntries().stream().map(RegistryObject::get).forEach(event::accept);
        }
    }
}
