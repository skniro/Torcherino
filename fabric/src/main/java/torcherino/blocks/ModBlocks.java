package torcherino.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.block.JackoLanterinoBlock;
import torcherino.block.LanterinoBlock;
import torcherino.block.TorcherinoBlock;
import torcherino.block.WallTorcherinoBlock;
import torcherino.block.entity.TorcherinoBlockEntity;
import torcherino.temp.TocherinoBlockEntityType;

import java.util.Map;

public final class ModBlocks {
    public static final ModBlocks INSTANCE = new ModBlocks();

    public void initialize() {
        Map<ResourceLocation, Tier> tiers = TorcherinoAPI.INSTANCE.getTiers();
        tiers.forEach((tierId, tier) -> {
            if (!tierId.getNamespace().equals(Torcherino.MOD_ID)) {
                return;
            }
            ResourceLocation torcherinoId = id(tierId, "torcherino");
            ResourceLocation jackoLanterinoId = id(tierId, "lanterino");
            ResourceLocation lanterinoId = id(tierId, "lantern");
            ParticleOptions particleEffect = (SimpleParticleType) Registry.PARTICLE_TYPE.get(id(tierId, "flame"));
            TorcherinoBlock torcherinoBlock = new TorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.TORCH), tierId, particleEffect);
            this.registerAndBlacklist(torcherinoId, torcherinoBlock);
            WallTorcherinoBlock torcherinoWallBlock = new WallTorcherinoBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).dropsLike(torcherinoBlock), tierId, particleEffect);
            this.registerAndBlacklist(new ResourceLocation(torcherinoId.getNamespace(), "wall_" + torcherinoId.getPath()), torcherinoWallBlock);
            JackoLanterinoBlock jackoLanterinoBlock = new JackoLanterinoBlock(BlockBehaviour.Properties.copy(Blocks.JACK_O_LANTERN), tierId);
            this.registerAndBlacklist(jackoLanterinoId, jackoLanterinoBlock);
            LanterinoBlock lanterinoBlock = new LanterinoBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN), tierId);
            this.registerAndBlacklist(lanterinoId, lanterinoBlock);
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                this.setRenderType(torcherinoBlock);
                this.setRenderType(torcherinoWallBlock);
                this.setRenderType(lanterinoBlock);
            }
            StandingAndWallBlockItem torcherinoItem = new StandingAndWallBlockItem(torcherinoBlock, torcherinoWallBlock,
                    new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS));
            Registry.register(Registry.ITEM, torcherinoId, torcherinoItem);
            BlockItem jackoLanterinoItem = new BlockItem(jackoLanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, jackoLanterinoId, jackoLanterinoItem);
            BlockItem lanterinoItem = new BlockItem(lanterinoBlock, new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS));
            Registry.register(Registry.ITEM, lanterinoId, lanterinoItem);
        });
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Torcherino.MOD_ID, "torcherino"),
                new TocherinoBlockEntityType(TorcherinoBlockEntity::new, null));
    }

    @Environment(EnvType.CLIENT)
    private void setRenderType(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutoutMipped());
    }

    private void registerAndBlacklist(ResourceLocation id, Block block) {
        Registry.register(Registry.BLOCK, id, block);
        TorcherinoAPI.INSTANCE.blacklistBlock(id);
    }

    private ResourceLocation id(ResourceLocation tierID, String type) {
        if (tierID.getPath().equals("normal")) {
            return new ResourceLocation(Torcherino.MOD_ID, type);
        }
        return new ResourceLocation(Torcherino.MOD_ID, tierID.getPath() + '_' + type);
    }
}
