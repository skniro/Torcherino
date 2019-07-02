package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
public class TorcherinoImpl implements TorcherinoAPI
{
    public static final TorcherinoImpl INSTANCE = new TorcherinoImpl();

    private final Logger LOGGER = LogManager.getLogger("torcherino-api");
    private final HashMap<Identifier, Tier> localTiers;
    private final HashMap<Identifier, Tier> remoteTiers;
    private final HashSet<Block> torcherinoBlocks;
    private final HashSet<Block> blacklistedBlocks;
    private final HashSet<BlockEntityType> blacklistedBlockEntities;

    private TorcherinoImpl()
    {
        localTiers = new HashMap<>();
        // On Server this will always be empty.
        // On Client this will always be a copy of the server's tier (even on integrated server)
        remoteTiers = new HashMap<>();
        torcherinoBlocks = new HashSet<>();
        blacklistedBlocks = new HashSet<>();
        blacklistedBlockEntities = new HashSet<>();
    }

    @Override
    public ImmutableMap<Identifier, Tier> getTiers()
    {
        return ImmutableMap.copyOf(localTiers);
    }

    @Override
    public Tier getTier(Identifier tierIdentifier)
    {
        return remoteTiers.getOrDefault(tierIdentifier, null);
    }

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
        if (Registry.BLOCK.containsId(blockIdentifier))
        {
            Block block = Registry.BLOCK.get(blockIdentifier);
            if (blacklistedBlocks.contains(block))
            {
                LOGGER.error("Block with id {} has already been blacklisted.", blockIdentifier);
                return false;
            }
            else
            {
                blacklistedBlocks.add(block);
                return true;
            }
        }
        LOGGER.error("No such block exists with id {}.", blockIdentifier);
        return false;
    }

    @Override
    public boolean blacklistBlock(Block block)
    {
        if (blacklistedBlocks.contains(block))
        {
            LOGGER.error("Block with id {} has already been blacklisted.", Registry.BLOCK.getId(block));
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
        if (Registry.BLOCK_ENTITY.containsId(blockEntityIdentifier))
        {
            BlockEntityType blockEntityType = Registry.BLOCK_ENTITY.get(blockEntityIdentifier);
            if (blacklistedBlockEntities.contains(blockEntityType))
            {
                LOGGER.error("Block entity with id {} has already been blacklisted.", blockEntityIdentifier);
                return false;
            }
            blacklistedBlockEntities.add(blockEntityType);
            return true;
        }
        LOGGER.error("No such block entity exists with id {}.", blockEntityIdentifier);
        return false;
    }

    @Override
    public boolean blacklistBlockEntity(BlockEntityType blockEntityType)
    {
        if (blacklistedBlockEntities.contains(blockEntityType))
        {
            LOGGER.error("Block entity with id {} has already been blacklisted.", Registry.BLOCK_ENTITY.getId(blockEntityType));
            return false;
        }
        blacklistedBlockEntities.add(blockEntityType);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(Block block)
    {
        return false;
    }

    @Override
    public boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType)
    {
        return false;
    }

    @Override
    public boolean registerTorcherinoBlock(Block block)
    {
        if(torcherinoBlocks.contains(block))
        {
            LOGGER.error("Block with id {} has already been registered as a torcherino.", Registry.BLOCK.getId(block));
            return false;
        }
        torcherinoBlocks.add(block);
        blacklistBlock(block);
        return true;
    }

    @Override
    public ImmutableSet<Block> getTorcherinoBlocks()
    {
        return ImmutableSet.copyOf(torcherinoBlocks);
    }
}
