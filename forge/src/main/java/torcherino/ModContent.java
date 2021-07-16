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
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Torcherino.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Torcherino.MOD_ID);

    public static void initialise(final IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLE_TYPES.register(bus);
        TILE_ENTITIES.register(bus);
        TILE_ENTITIES.register("torcherino", TORCHERINO_TILE_ENTITY.delegate);
        TorcherinoAPI.INSTANCE.blacklistBlockEntity(TORCHERINO_TILE_ENTITY);
        TorcherinoAPI.INSTANCE.getTiers().keySet().forEach(ModContent::register);
    }

    public static final BlockEntityType<TorcherinoBlockEntity> TORCHERINO_TILE_ENTITY = new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null);

    private static String getPath(final ResourceLocation tierID, final String type) {
        return (tierID.getPath().equals("normal") ? "" : tierID.getPath() + "_") + type;
    }

    private static void register(final ResourceLocation tierID) {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID)) {
            final SimpleParticleType particleType = new SimpleParticleType(false);
            PARTICLE_TYPES.register(getPath(tierID, "flame"), particleType.delegate);
            final TorcherinoBlock standingBlock = new TorcherinoBlock(tierID, particleType);
            final WallTorcherinoBlock wallBlock = new WallTorcherinoBlock(tierID, standingBlock, particleType);
            final Item torcherinoItem = new StandingAndWallBlockItem(standingBlock, wallBlock, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            final JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(tierID);
            final Item jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            final LanterinoBlock lanterinoBlock = new LanterinoBlock(tierID);
            final Item lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            final String torcherinoPath = getPath(tierID, "torcherino");
            final String jackoLanterinoPath = getPath(tierID, "lanterino");
            final String lanterinoPath = getPath(tierID, "lantern");
            BLOCKS.register(torcherinoPath, standingBlock.delegate);
            BLOCKS.register("wall_" + torcherinoPath, wallBlock.delegate);
            BLOCKS.register(jackoLanterinoPath, jackoLanterinoBlock.delegate);
            BLOCKS.register(lanterinoPath, lanterinoBlock.delegate);
            TorcherinoAPI.INSTANCE.blacklistBlock(standingBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(wallBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(jackoLanterinoBlock);
            TorcherinoAPI.INSTANCE.blacklistBlock(lanterinoBlock);
            if (FMLLoader.getDist().isClient()) {
                Minecraft.getInstance().submitAsync(() ->
                {
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
