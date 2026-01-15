package torcherino.blocks;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopperBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.CopperLanterinoBlock;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.api.LanterinoOxidizableRegistry;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WallTorcherinoBlock;
import torcherino.block.WeatheringLanterinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class ModBlocks {
    public static final ModBlocks INSTANCE = new ModBlocks();
    Set<Block> allBlocks = new HashSet<>();
    public WeatheringCopperBlocks copperLanterino;

    public void initialize() {
        Map<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> allBlocks.forEach((entries::accept)));

        tiers.forEach((tierId, tier) -> {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) {
                return;
            }
            Identifier torcherinoId = id(tierId, "torcherino");
            Identifier jackoLanterinoId = id(tierId, "lanterino");
            Identifier lanterinoId = id(tierId, "lantern");
            Identifier copperlanterinoId = id(tierId, "copper_lantern");
            SimpleParticleType particleEffect = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.getValue(id(tierId, "flame"));
            TorcherinoBlock torcherinoBlock = new TorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock(torcherinoId.getPath())), tierId, particleEffect);
            this.registerAndBlacklist(torcherinoId, torcherinoBlock);
            WallTorcherinoBlock torcherinoWallBlock = new WallTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock("wall_" + torcherinoId.getPath())).overrideDescription(torcherinoBlock.getDescriptionId()).overrideLootTable(torcherinoBlock.getLootTable()), tierId, particleEffect);
            this.registerAndBlacklist(Identifier.fromNamespaceAndPath(torcherinoId.getNamespace(), "wall_" + torcherinoId.getPath()), torcherinoWallBlock);
            JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).setId(Torcherino.KeyofBlock(jackoLanterinoId.getPath())).pushReaction(PushReaction.IGNORE), tierId);
            this.registerAndBlacklist(jackoLanterinoId, jackoLanterinoBlock);
            LanterinoBlock lanterinoBlock = new LanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).pushReaction(PushReaction.IGNORE).setId(Torcherino.KeyofBlock(lanterinoId.getPath())), tierId);
            this.registerAndBlacklist(lanterinoId, lanterinoBlock);
            this.copperLanterino = WeatheringCopperBlocks.create(
                    copperlanterinoId.getPath(),
                    this::register,
                    (props) -> new CopperLanterinoBlock(props, tierId),
                    (state, props) -> new WeatheringLanterinoBlock(state, props, tierId),
                    (weatherState) -> BlockBehaviour.Properties.of()
                                                               .mapColor(MapColor.METAL)
                                                               .forceSolidOn()
                                                               .strength(3.5F)
                                                               .sound(SoundType.LANTERN)
                                                               .lightLevel(state -> 15)
                                                               .noOcclusion()
                                                               .pushReaction(PushReaction.IGNORE)
                                                               .randomTicks()
            );
            LanterinoOxidizableRegistry.registerWeatheringSet(copperLanterino);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                this.setRenderType(torcherinoBlock);
                this.setRenderType(torcherinoWallBlock);
                this.setRenderType(lanterinoBlock);
                copperLanterino.forEach(this::setRenderType);
            }
            StandingAndWallBlockItem torcherinoItem = new StandingAndWallBlockItem(torcherinoBlock, torcherinoWallBlock, Direction.DOWN, new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(torcherinoId.getPath())));
            Registry.register(BuiltInRegistries.ITEM, torcherinoId, torcherinoItem);
            BlockItem jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(jackoLanterinoId.getPath())));
            Registry.register(BuiltInRegistries.ITEM, jackoLanterinoId, jackoLanterinoItem);
            BlockItem lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().useBlockDescriptionPrefix().setId(Torcherino.KeyofItem(lanterinoId.getPath())));
            Registry.register(BuiltInRegistries.ITEM, lanterinoId, lanterinoItem);
            copperLanterino.forEach(block -> {
                Identifier itemId = BuiltInRegistries.BLOCK.getKey(block);
                BlockItem item = new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .setId(Torcherino.KeyofItem(itemId.getPath())));
                Registry.register(BuiltInRegistries.ITEM, itemId, item);
            });
        });
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, "torcherino"),
                FabricBlockEntityTypeBuilder.create(TorcherinoBlockEntity::new, allBlocks.toArray(new Block[0])).build(null));
    }

    @Environment(EnvType.CLIENT)
    private void setRenderType(Block block) {
        ChunkSectionLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
    }

    private Block register(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        Block block = (Block)factory.apply(settings.setId(Torcherino.KeyofBlock(id)));
        TorcherinoAPI.INSTANCE.blacklistBlock(block);
        allBlocks.add(block);
        return Registry.register(BuiltInRegistries.BLOCK, Torcherino.KeyofBlock(id), block);
    }

    private void registerAndBlacklist(Identifier id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        TorcherinoAPI.INSTANCE.blacklistBlock(id);
        allBlocks.add(block);
    }

    private Identifier id(Identifier tierID, String type) {
        if (tierID.getPath().equals("normal")) {
            return Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, type);
        }
        return Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
