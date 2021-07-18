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
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import torcherino.api.TorcherinoAPI;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WallTorcherinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.temp.TocherinoBlockEntityType;

@Mod.EventBusSubscriber(modid = Torcherino.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModContent {
    private static final BlockEntityType<TorcherinoBlockEntity> TORCHERINO_TILE_ENTITY = new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Torcherino.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Torcherino.MOD_ID);

    public static void initialise(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLE_TYPES.register(bus);
        TILE_ENTITIES.register(bus);
        TILE_ENTITIES.register("torcherino", TORCHERINO_TILE_ENTITY.delegate);
        TorcherinoAPI.INSTANCE.blacklistBlockEntity(TORCHERINO_TILE_ENTITY);
        TorcherinoAPI.INSTANCE.getTiers().keySet().forEach(ModContent::register);
    }

    private static String getPath(ResourceLocation tierID, String type) {
        return (tierID.getPath().equals("normal") ? "" : tierID.getPath() + "_") + type;
    }

    private static void register(ResourceLocation tierID) {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID)) {
            SimpleParticleType particleType = new SimpleParticleType(false);
            PARTICLE_TYPES.register(getPath(tierID, "flame"), particleType.delegate);
            TorcherinoBlock standingBlock = new TorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.TORCH), tierID, particleType);
            WallTorcherinoBlock wallBlock = new WallTorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).dropsLike(standingBlock), tierID, particleType);
            Item torcherinoItem = new StandingAndWallBlockItem(standingBlock, wallBlock, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(BlockBehaviour.Properties.copy(Blocks.JACK_O_LANTERN), tierID);
            Item jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            LanterinoBlock lanterinoBlock = new LanterinoBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN), tierID);
            Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            String torcherinoPath = getPath(tierID, "torcherino");
            String jackoLanterinoPath = getPath(tierID, "lanterino");
            String lanterinoPath = getPath(tierID, "lantern");
            BLOCKS.register(torcherinoPath, standingBlock.delegate);
            BLOCKS.register("wall_" + torcherinoPath, wallBlock.delegate);
            BLOCKS.register(jackoLanterinoPath, jackoLanterinoBlock.delegate);
            BLOCKS.register(lanterinoPath, lanterinoBlock.delegate);
            TorcherinoAPI.INSTANCE.blacklistBlock(standingBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(wallBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(jackoLanterinoBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(lanterinoBlock);
            if (FMLLoader.getDist().isClient()) {
                Minecraft.getInstance().submitAsync(() -> {
                    ItemBlockRenderTypes.setRenderLayer(standingBlock, RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(wallBlock, RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(lanterinoBlock, RenderType.cutout());
                });
            }
            ITEMS.register(torcherinoPath, torcherinoItem.delegate);
            ITEMS.register(jackoLanterinoPath, jackoLanterinoItem.delegate);
            ITEMS.register(lanterinoPath, lanterinoItem.delegate);
        }
    }

    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        PARTICLE_TYPES.getEntries().forEach(registryObject -> Minecraft.getInstance().particleEngine.register((SimpleParticleType) registryObject.get(),
                FlameParticle.Provider::new));
    }
}
