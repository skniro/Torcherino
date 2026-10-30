package torcherino_unofficial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.blocks.ModBlocks;
import torcherino_unofficial.block.CopperLanterinoBlock;
import torcherino_unofficial.block.WeatheringCopperBlocks;
import torcherino_unofficial.block.WeatheringLanterinoBlock;
import torcherino_unofficial.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.ModItemBlockRenderTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class UnofficialContent {
    public static final UnofficialContent INSTANCE = new UnofficialContent();
    Set<Block> allBlocks = new HashSet<>();
    public WeatheringCopperBlocks copperLanterino;

    public void initialise() {
        Map<Identifier, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> allBlocks.forEach((entries::accept)));
        tiers.forEach((tierId, tier) -> {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) {
                return;
            }
            Identifier copperlanterinoId = id(tierId, "copper_lantern");
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
                copperLanterino.forEach(this::setRenderType);
            }
            copperLanterino.forEach(block -> {
                Identifier itemId = BuiltInRegistries.BLOCK.getKey(block);
                BlockItem item = new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .setId(Torcherino.KeyofItem(itemId.getPath())));
                Registry.register(BuiltInRegistries.ITEM, itemId, item);
            });
        });
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, "copper_lantern"),
                FabricBlockEntityTypeBuilder.create((pos, state) -> new TorcherinoBlockEntity(CopperLanterinoBlock.copperBlockEntityType(), pos, state), allBlocks.toArray(new Block[0])).build(null));
    }

    @Environment(EnvType.CLIENT)
    private void setRenderType(Block block) {
        ModItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT);
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
