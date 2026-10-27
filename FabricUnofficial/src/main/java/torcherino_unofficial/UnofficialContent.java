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
import net.minecraft.world.level.block.WeatheringCopperBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;
import torcherino_unofficial.block.CopperLanterinoBlock;
import torcherino_unofficial.block.WeatheringLanterinoBlock;
import torcherino_unofficial.block.api.LanterinoOxidizableRegistry;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.client.ModItemBlockRenderTypes;

import java.util.ArrayList;
import java.util.List;

public final class UnofficialContent {
    private static final List<Block> COPPER_BLOCKS = new ArrayList<>();
    private static final List<Item> COPPER_ITEMS = new ArrayList<>();

    public static void initialise() {
        TorcherinoAPI.INSTANCE.getTiers().forEach((tierId, tier) -> {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) {
                return;
            }
            Identifier copperId = id(tierId, "copper_lantern");
            WeatheringCopperBlocks copper = WeatheringCopperBlocks.create(
                    copperId.getPath(),
                    (path, factory, props) -> {
                        Block block = factory.apply(props.setId(Torcherino.KeyofBlock(path)));
                        TorcherinoAPI.INSTANCE.blacklistBlock(block);
                        COPPER_BLOCKS.add(block);
                        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, path), block);
                    },
                    props -> new CopperLanterinoBlock(props, tierId),
                    (state, props) -> new WeatheringLanterinoBlock(state, props, tierId),
                    weatherState -> copperProperties()
            );
            LanterinoOxidizableRegistry.registerWeatheringSet(copper);
            copper.forEach(block -> {
                Identifier itemId = BuiltInRegistries.BLOCK.getKey(block);
                BlockItem item = new BlockItem(block, new Item.Properties()
                        .useBlockDescriptionPrefix()
                        .setId(Torcherino.KeyofItem(itemId.getPath())));
                Registry.register(BuiltInRegistries.ITEM, itemId, item);
                COPPER_ITEMS.add(item);
            });
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                copper.forEach(UnofficialContent::setRenderType);
            }
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> COPPER_ITEMS.forEach(entries::accept));

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, "copper_lantern"),
                FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new TorcherinoBlockEntity(CopperLanterinoBlock.copperBlockEntityType(), pos, state),
                        COPPER_BLOCKS.toArray(new Block[0])
                ).build(null));
    }

    private static BlockBehaviour.Properties copperProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .forceSolidOn()
                .strength(3.5F)
                .sound(SoundType.LANTERN)
                .lightLevel(state -> 15)
                .noOcclusion()
                .pushReaction(PushReaction.IGNORE)
                .randomTicks();
    }

    @Environment(EnvType.CLIENT)
    private static void setRenderType(Block block) {
        ModItemBlockRenderTypes.setRenderLayer(block, ChunkSectionLayer.CUTOUT);
    }

    private static Identifier id(Identifier tierID, String type) {
        if (tierID.getPath().equals("normal")) {
            return Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, type);
        }
        return Identifier.fromNamespaceAndPath(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }

    private UnofficialContent() {
    }
}
