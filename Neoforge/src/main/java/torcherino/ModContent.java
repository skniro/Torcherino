package torcherino;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import torcherino.api.TorcherinoAPI;
import torcherino.block.ForgeTorcherinoBlock;
import torcherino.block.ForgeWallTorcherinoBlock;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.particle.TorcherinoParticleTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Torcherino.MOD_ID)
public final class ModContent {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Torcherino.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Torcherino.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Torcherino.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Torcherino.MOD_ID);

    public static void initialise(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TorcherinoParticleTypes.PARTICLE_TYPES.register(bus);
        TILE_ENTITIES.register(bus);


        TILE_ENTITIES.register("torcherino", () ->  new BlockEntityType<>(TorcherinoBlockEntity::new, BLOCKS.getEntries().stream().map(Supplier::get).toList().toArray(new Block[0])));
        toBlacklist.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, "torcherino"));
        TorcherinoAPI.INSTANCE.getTiers().keySet().forEach(ModContent::register);
    }

    private static String getPath(Identifier tierID, String type) {
        return (tierID.getPath().equals("normal") ? "" : tierID.getPath() + "_") + type;
    }


    static Supplier<TorcherinoBlock> b;
    private static void register(Identifier tierID) {
        if (tierID.getNamespace().equals(Torcherino.MOD_ID)) {
            String torcherinoPath = getPath(tierID, "torcherino");
            String jackoLanterinoPath = getPath(tierID, "lanterino");
            String lanterinoPath = getPath(tierID, "lantern");

            toBlacklist.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, torcherinoPath));
            toBlacklist.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, "wall_" + torcherinoPath));
            toBlacklist.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, jackoLanterinoPath));
            toBlacklist.add(Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, lanterinoPath));

            Supplier<ForgeTorcherinoBlock> standingBlock = BLOCKS.register(torcherinoPath, () -> new ForgeTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).pushReaction(PushReaction.IGNORE_ENTITY).setId(Torcherino.KeyofBlock(torcherinoPath)), tierID));
            Supplier<ForgeWallTorcherinoBlock> wallBlock = BLOCKS.register("wall_" + torcherinoPath, () -> new ForgeWallTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).pushReaction(PushReaction.IGNORE_ENTITY).setId(Torcherino.KeyofBlock("wall_" + torcherinoPath)).overrideDescription(standingBlock.get().getDescriptionId()).overrideLootTable(standingBlock.get().getLootTable()), tierID));
            Supplier<JackoLanterinoBlock> jackoLanterinoBlock = BLOCKS.register(jackoLanterinoPath, () -> new JackoLanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).pushReaction(PushReaction.IGNORE_ENTITY).setId(Torcherino.KeyofBlock(jackoLanterinoPath)), tierID));
            Supplier<LanterinoBlock> lanterinoBlock = BLOCKS.register(lanterinoPath, () -> new LanterinoBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().strength(3.5F).sound(SoundType.LANTERN).lightLevel(state -> 15).noOcclusion().pushReaction(PushReaction.IGNORE_ENTITY).randomTicks().setId(Torcherino.KeyofBlock(lanterinoPath)), tierID));

            ITEMS.register(torcherinoPath, () -> new StandingAndWallBlockItem(standingBlock.get(), wallBlock.get(), Direction.DOWN, new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(torcherinoPath))));
            ITEMS.register(jackoLanterinoPath, () -> new BlockItem(jackoLanterinoBlock.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(jackoLanterinoPath))));
            ITEMS.register(lanterinoPath, () -> new BlockItem(lanterinoBlock.get(), new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(lanterinoPath))));

            if (FMLEnvironment.getDist().isClient()) {
                ClientHelper.registerCutout(standingBlock);
                ClientHelper.registerCutout(wallBlock);
                ClientHelper.registerCutout(lanterinoBlock);
            }
        }
    }

    private static final Set<Identifier> toBlacklist = new HashSet<>();
    //public static final Map<Identifier, BiMap<Block, Block>> WAXABLES = new HashMap<>();
    //public static final Map<Identifier, BiMap<Block, Block>> NEXT_BY_BLOCK = new HashMap<>();

    @SubscribeEvent
    public static void blackliststuff(final FMLCommonSetupEvent event) {
        for (Identifier block : toBlacklist){
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
            ITEMS.getEntries().stream().map(Supplier::get).forEach(event::accept);
        }
    }
}
