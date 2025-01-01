package torcherino.blocks;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WallTorcherinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ModBlocks {
    public static final ModBlocks INSTANCE = new ModBlocks();
    Set<Block> allBlocks = new HashSet<>();

    public void initialize() {
        Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> allBlocks.forEach((entries::accept)));

        tiers.forEach((tierId, tier) -> {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) {
                return;
            }
            ResourceLocation torcherinoId = id(tierId, "torcherino");
            ResourceLocation jackoLanterinoId = id(tierId, "lanterino");
            ResourceLocation lanterinoId = id(tierId, "lantern");
            SimpleParticleType particleEffect = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(id(tierId, "flame"));
            TorcherinoBlock torcherinoBlock = new TorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).pushReaction(PushReaction.IGNORE), tierId, particleEffect);
            this.registerAndBlacklist(torcherinoId, torcherinoBlock);
            WallTorcherinoBlock torcherinoWallBlock = new WallTorcherinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WALL_TORCH).pushReaction(PushReaction.IGNORE).dropsLike(torcherinoBlock), tierId, particleEffect);
            this.registerAndBlacklist(new ResourceLocation(torcherinoId.getNamespace(), "wall_" + torcherinoId.getPath()), torcherinoWallBlock);
            JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JACK_O_LANTERN).pushReaction(PushReaction.IGNORE), tierId);
            this.registerAndBlacklist(jackoLanterinoId, jackoLanterinoBlock);
            LanterinoBlock lanterinoBlock = new LanterinoBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).pushReaction(PushReaction.IGNORE), tierId);
            this.registerAndBlacklist(lanterinoId, lanterinoBlock);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                this.setRenderType(torcherinoBlock);
                this.setRenderType(torcherinoWallBlock);
                this.setRenderType(lanterinoBlock);
            }
            StandingAndWallBlockItem torcherinoItem = new StandingAndWallBlockItem(torcherinoBlock, torcherinoWallBlock, new Item.Properties(), Direction.DOWN);
            Registry.register(BuiltInRegistries.ITEM, torcherinoId, torcherinoItem);
            BlockItem jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, jackoLanterinoId, jackoLanterinoItem);
            BlockItem lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, lanterinoId, lanterinoItem);
        });
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Torcherino.MOD_ID, "torcherino"),
                BlockEntityType.Builder.of(TorcherinoBlockEntity::new, allBlocks.toArray(new Block[0])).build(null));
    }

    @Environment(EnvType.CLIENT)
    private void setRenderType(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
    }

    private void registerAndBlacklist(ResourceLocation id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        TorcherinoAPI.INSTANCE.blacklistBlock(id);
        allBlocks.add(block);
    }

    private ResourceLocation id(ResourceLocation tierID, String type) {
        if (tierID.getPath().equals("normal")) {
            return new ResourceLocation(Torcherino.MOD_ID, type);
        }
        return new ResourceLocation(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
