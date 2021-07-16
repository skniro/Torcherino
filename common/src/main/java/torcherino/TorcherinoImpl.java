package torcherino;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
        if (Registry.BLOCK.containsKey(block)) {
            final Block b = Registry.BLOCK.get(block);
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
            LOGGER.warn("Block with id {} is already blacklisted.", Registry.BLOCK.getKey(block));
            return false;
        }
        blacklistedBlocks.add(block);
        return true;
    }

    // todo: remove in 15.0.0
    @Override
    public boolean blacklistTileEntity(ResourceLocation blockEntityId) {
        return this.blacklistBlockEntity(blockEntityId);
    }

    // todo: remove in 15.0.0
    @Override
    public boolean blacklistTileEntity(BlockEntityType<? extends BlockEntity> blockEntityType) {
        return blacklistBlockEntity(blockEntityType);
    }

    @Override
    public boolean isBlockBlacklisted(Block block) {
        return blacklistedBlocks.contains(block);
    }

    // todo: remove in 15.0.0
    @Override
    public boolean isTileEntityBlacklisted(BlockEntityType<? extends BlockEntity> blockEntityType) {
        return isBlockEntityBlacklisted(blockEntityType);
    }

    @Override
    public boolean blacklistBlockEntity(ResourceLocation blockEntityId) {
        if (Registry.BLOCK_ENTITY_TYPE.containsKey(blockEntityId)) {
            final BlockEntityType<?> type = Registry.BLOCK_ENTITY_TYPE.get(blockEntityId);
            if (blacklistedTiles.contains(type)) {
                LOGGER.warn("BlockEntityType with id {} is already blacklisted.", blockEntityId);
                return false;
            }
            blacklistedTiles.add(type);
            return true;
        }
        LOGGER.warn("BlockEntityType with id {} does not exist.", blockEntityId);
        return false;
    }

    @Override
    public boolean blacklistBlockEntity(BlockEntityType<?> blockEntityType) {
        if (blacklistedTiles.contains(blockEntityType)) {
            LOGGER.warn("BlockEntityType with id {} is already blacklisted.", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType));
            return false;
        }
        blacklistedTiles.add(blockEntityType);
        return true;
    }

    @Override
    public boolean isBlockEntityBlacklisted(BlockEntityType<?> blockEntityType) {
        return blacklistedTiles.contains(blockEntityType);
    }

    // Do not use
    public void setRemoteTiers(Map<ResourceLocation, Tier> tiers) {
        remoteTiers = tiers;
    }

    public ImmutableMap<ResourceLocation, Tier> getTiers() {
        return ImmutableMap.copyOf(localTiers);
    }

    @Override
    public Tier getTier(ResourceLocation name) {
        return remoteTiers.getOrDefault(name, null);
    }
}
