package torcherino;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class TorcherinoImpl implements TorcherinoAPI {
    private final Logger logger = LogManager.getLogger("torcherino-api");
    private final Map<ResourceLocation, Tier> localTiers = new HashMap<>();
    private final Set<Block> blacklistedBlocks = new HashSet<>();
    private final Set<BlockEntityType<?>> blacklistedTiles = new HashSet<>();
    private Map<ResourceLocation, Tier> remoteTiers = new HashMap<>();

    public void registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange) {
        if (localTiers.containsKey(name)) {
            logger.warn("Tier with id {} has already been registered.", name);
            return;
        }
        Tier tier = new Tier(maxSpeed, xzRange, yRange);
        localTiers.put(name, tier);
    }

    public boolean blacklistBlock(ResourceLocation blockId) {
        Optional<Block> block = Registry.BLOCK.getOptional(blockId);
        if (block.isPresent()) {
            if (blacklistedBlocks.contains(block.get())) {
                logger.warn("Block with id {} is already blacklisted.", block);
                return false;
            }
            blacklistedBlocks.add(block.get());
            return true;
        }
        logger.warn("Block with id {} does not exist.", block);
        return false;
    }

    @Override
    public boolean blacklistBlock(Block block) {
        if (blacklistedBlocks.contains(block)) {
            logger.warn("Block with id {} is already blacklisted.", Registry.BLOCK.getKey(block));
            return false;
        }
        blacklistedBlocks.add(block);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(Block block) {
        return blacklistedBlocks.contains(block);
    }

    @Override
    public boolean blacklistBlockEntity(ResourceLocation blockEntityTypeId) {
        Optional<BlockEntityType<?>> blockEntityType = Registry.BLOCK_ENTITY_TYPE.getOptional(blockEntityTypeId);
        if (blockEntityType.isPresent()) {
            if (blacklistedTiles.contains(blockEntityType.get())) {
                logger.warn("BlockEntityType with id {} is already blacklisted.", blockEntityTypeId);
                return false;
            }
            blacklistedTiles.add(blockEntityType.get());
            return true;
        }
        logger.warn("BlockEntityType with id {} does not exist.", blockEntityTypeId);
        return false;
    }

    @Override
    public boolean blacklistBlockEntity(BlockEntityType<?> blockEntityType) {
        if (blacklistedTiles.contains(blockEntityType)) {
            logger.warn("BlockEntityType with id {} is already blacklisted.", Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType));
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
