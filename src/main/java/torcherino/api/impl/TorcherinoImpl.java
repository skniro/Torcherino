package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.HashSet;

/**
 * DO NOT USE THIS CLASS DIRECTLY. Use TorcherinoAPI.INSTANCE instead. Why? anything in this class is subject to change where as anything in the API won't be
 * removed without significant warning time. e.g. a minecraft version update or major mod update.
 */
@SuppressWarnings("SpellCheckingInspection")
public class TorcherinoImpl implements TorcherinoAPI
{
    public static final TorcherinoImpl INSTANCE = new TorcherinoImpl();

    private final Logger LOGGER = LogManager.getLogger("torcherino-api");
    private final HashMap<Identifier, Tier> localTiers;
    private final HashSet<Block> blacklistedBlocks;
    private final HashSet<BlockEntityType> blacklistedBlockEntities;
    private HashMap<Identifier, Tier> remoteTiers;

    private TorcherinoImpl()
    {
        localTiers = new HashMap<>();
        blacklistedBlocks = new HashSet<>();
        blacklistedBlockEntities = new HashSet<>();
    }

    @Override
    public ImmutableMap<Identifier, Tier> getTiers() { return ImmutableMap.copyOf(localTiers); }

    @Override
    public Tier getTier(Identifier tierIdentifier) { return remoteTiers.getOrDefault(tierIdentifier, null); }

    @Override
    public boolean registerTier(Identifier tierIdentifier, int maxSpeed, int xzRange, int yRange)
    {
        Tier tier = new Tier(maxSpeed, xzRange, yRange);
        if (localTiers.containsKey(tierIdentifier))
        {
            LOGGER.error("Tier with id {} has already been declared.", tierIdentifier);
            return false;
        }
        localTiers.put(tierIdentifier, tier);
        return true;
    }

    @Override
    public boolean blacklistBlock(Identifier blockIdentifier)
    {
        Block block = (Block) ((SimpleRegistry) Registry.BLOCK).get(blockIdentifier);
        if (block == null)
        {
            LOGGER.error("No such block exists with id {}.", blockIdentifier);
            return false;
        }
        else
        {
            if (blacklistedBlocks.contains(block))
            {
                LOGGER.warn("Block with id {} has already been blacklisted.", blockIdentifier);
                return false;
            }
            else
            {
                blacklistedBlocks.add(block);
                return true;
            }
        }
    }

    @Override
    public boolean blacklistBlock(Block block)
    {
        if (blacklistedBlocks.contains(block))
        {
            LOGGER.warn("Block with id {} has already been blacklisted.", Registry.BLOCK.getId(block));
            return false;
        }
        else
        {
            blacklistedBlocks.add(block);
            return true;
        }
    }

    @Override
    public boolean blacklistBlockEntity(Identifier blockEntityIdentifier)
    {
        BlockEntityType blockEntityType = (BlockEntityType) ((SimpleRegistry) Registry.BLOCK_ENTITY).get(blockEntityIdentifier);
        if (blockEntityType == null)
        {
            LOGGER.error("No such block entity exists with id {}.", blockEntityIdentifier);
            return false;
        }
        else
        {
            if (blacklistedBlockEntities.contains(blockEntityType))
            {
                LOGGER.warn("Block entity with id {} has already been blacklisted.", blockEntityIdentifier);
                return false;
            }
            blacklistedBlockEntities.add(blockEntityType);
            return true;
        }
    }

    @Override
    public boolean blacklistBlockEntity(BlockEntityType blockEntityType)
    {
        if (blacklistedBlockEntities.contains(blockEntityType))
        {
            LOGGER.warn("Block entity with id {} has already been blacklisted.", Registry.BLOCK_ENTITY.getId(blockEntityType));
            return false;
        }
        blacklistedBlockEntities.add(blockEntityType);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(Block block) { return blacklistedBlocks.contains(block); }

    @Override
    public boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType) { return blacklistedBlockEntities.contains(blockEntityType); }

    // Internal do not use.
    public void setRemoteTiers(HashMap<Identifier, Tier> tiers) { remoteTiers = tiers; }
}
