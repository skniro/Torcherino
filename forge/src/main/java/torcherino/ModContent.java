package torcherino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import torcherino.api.TorcherinoAPI;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WallTorcherinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Torcherino.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Torcherino.MOD_ID);

    public static void initialise(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLE_TYPES.register(bus);
        TILE_ENTITIES.register(bus);


        TILE_ENTITIES.register("torcherino", () ->  BlockEntityType.Builder.of(TorcherinoBlockEntity::new, BLOCKS.getEntries().stream().map(RegistryObject::get).toList().toArray(new Block[0])).build(null));toBlacklist.add(new ResourceLocation(Torcherino.MOD_ID, "torcherino"));
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

            toBlacklist.add(new ResourceLocation(Torcherino.MOD_ID, torcherinoPath));
            toBlacklist.add(new ResourceLocation(Torcherino.MOD_ID, "wall_" + torcherinoPath));
            toBlacklist.add(new ResourceLocation(Torcherino.MOD_ID, jackoLanterinoPath));
            toBlacklist.add(new ResourceLocation(Torcherino.MOD_ID, lanterinoPath));


            Supplier<SimpleParticleType> particleType = () -> new SimpleParticleType(false);
            PARTICLE_TYPES.register(getPath(tierID, "flame"), particleType);

            Supplier<TorcherinoBlock> standingBlock = BLOCKS.register(torcherinoPath, () -> new TorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.TORCH), tierID, particleType.get()));
            Supplier<WallTorcherinoBlock> wallBlock = BLOCKS.register("wall_" + torcherinoPath, () -> new WallTorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).dropsLike(standingBlock.get()), tierID, particleType.get()));
            Supplier<JackoLanterinoBlock> jackoLanterinoBlock = BLOCKS.register(jackoLanterinoPath, () -> new JackoLanterinoBlock(BlockBehaviour.Properties.copy(Blocks.JACK_O_LANTERN), tierID));
            Supplier<LanterinoBlock> lanterinoBlock = BLOCKS.register(lanterinoPath, () -> new LanterinoBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN), tierID));

            ITEMS.register(torcherinoPath, () -> new StandingAndWallBlockItem(standingBlock.get(), wallBlock.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
            ITEMS.register(jackoLanterinoPath, () -> new BlockItem(jackoLanterinoBlock.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
            ITEMS.register(lanterinoPath, () -> new BlockItem(lanterinoBlock.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

            if (FMLLoader.getDist().isClient()) {
                Minecraft.getInstance().submitAsync(() -> {
                    ItemBlockRenderTypes.setRenderLayer(standingBlock.get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(wallBlock.get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(lanterinoBlock.get(), RenderType.cutout());
                });
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
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        PARTICLE_TYPES.getEntries().forEach(registryObject -> Minecraft.getInstance().particleEngine.register((SimpleParticleType) registryObject.get(),
                FlameParticle.Provider::new));
    }
}
