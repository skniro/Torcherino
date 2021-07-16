package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TorcherinoImpl implements TorcherinoAPI {
    private final Logger LOGGER = LogManager.getLogger("torcherino-api");
    private final Map<ResourceLocation, Tier> localTiers = new HashMap<>();
    private final Set<Block> blacklistedBlocks = new HashSet<>();
    private final Set<BlockEntityType<?>> blacklistedTiles = new HashSet<>();
    private Map<ResourceLocation, Tier> remoteTiers = new HashMap<>();

    public boolean registerTier(final ResourceLocation name, final int maxSpeed, final int xzRange, final int yRange) {
        final Tier tier = new Tier(maxSpeed, xzRange, yRange);
        if (localTiers.containsKey(name)) {
            LOGGER.warn("Tier with id {} has already been registered.", name);
            return false;
        }
        localTiers.put(name, tier);
        return true;
    }

    public boolean blacklistBlock(final ResourceLocation block) {
        if (ForgeRegistries.BLOCKS.containsKey(block)) {
            final Block b = ForgeRegistries.BLOCKS.getValue(block);
            if (blacklistedBlocks.contains(b)) {
                LOGGER.warn("Block with id {} is already blacklisted.", block);
                return false;
            }
            blacklistedBlocks.add(b);
            return true;
        }
        LOGGER.warn("Block with id {} does not exist.", block);
        return false;
    }

    @Override
    public boolean blacklistBlock(final Block block) {
        if (blacklistedBlocks.contains(block)) {
            LOGGER.warn("Block with id {} is already blacklisted.", block.getRegistryName());
            return false;
        }
        blacklistedBlocks.add(block);
        return true;
    }

    @Override
    public boolean blacklistTileEntity(final ResourceLocation tileEntity) {
        if (ForgeRegistries.TILE_ENTITIES.containsKey(tileEntity)) {
            final BlockEntityType<?> type = ForgeRegistries.TILE_ENTITIES.getValue(tileEntity);
            if (blacklistedTiles.contains(type)) {
                LOGGER.warn("TileEntity with id {} is already blacklisted.", tileEntity);
                return false;
            }
            blacklistedTiles.add(type);
            return true;
        }
        LOGGER.warn("TileEntity with id {} does not exist.", tileEntity);
        return false;
    }

    @Override
    public boolean blacklistTileEntity(final BlockEntityType<? extends BlockEntity> tileEntity) {
        if (blacklistedTiles.contains(tileEntity)) {
            LOGGER.warn("TileEntity with id {} is already blacklisted.", tileEntity.getRegistryName());
            return false;
        }
        blacklistedTiles.add(tileEntity);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(final Block block) {
        return blacklistedBlocks.contains(block);
    }

    @Override
    public boolean isTileEntityBlacklisted(final BlockEntityType<? extends BlockEntity> tileEntityType) {
        return blacklistedTiles.contains(tileEntityType);
    }

    // Do not use
    public void setRemoteTiers(final Map<ResourceLocation, Tier> tiers) {
        remoteTiers = tiers;
    }

    public ImmutableMap<ResourceLocation, Tier> getTiers() {
        return ImmutableMap.copyOf(localTiers);
    }

    @Override
    public Tier getTier(final ResourceLocation name) {
        return remoteTiers.getOrDefault(name, null);
    }
}
