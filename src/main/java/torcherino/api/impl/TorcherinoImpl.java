package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TorcherinoImpl implements TorcherinoAPI
{
    private final Logger LOGGER = LogManager.getLogger("torcherino-api");
    private Map<ResourceLocation, Tier> clientTiers = new HashMap<>();
    private Map<ResourceLocation, Tier> serverTiers = new HashMap<>();

    private Set<Block> blacklistedBlocks = new HashSet<>();

    private Set<TileEntityType> blacklistedTiles = new HashSet<>();

    private Set<Block> torcherinoBlocks = new HashSet<>();

    public boolean registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange)
    {
        Tier tier = new Tier(maxSpeed, xzRange, yRange);
        if (clientTiers.containsKey(name))
        {
            LOGGER.warn("Tier with id {} has already been registered.", name);
            return false;
        }
        clientTiers.put(name, tier);
        return true;
    }

    public boolean blacklistBlock(ResourceLocation block)
    {
        if (ForgeRegistries.BLOCKS.containsKey(block))
        {
            Block b = ForgeRegistries.BLOCKS.getValue(block);
            if (blacklistedBlocks.contains(b))
            {
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
    public boolean blacklistBlock(Block block)
    {
        if (blacklistedBlocks.contains(block))
        {
            LOGGER.warn("Block with id {} is already blacklisted.", block.getRegistryName());
            return false;
        }
        blacklistedBlocks.add(block);
        return true;
    }

    @Override
    public boolean blacklistTileEntity(ResourceLocation tileEntity)
    {
        if (ForgeRegistries.TILE_ENTITIES.containsKey(tileEntity))
        {
            TileEntityType type = ForgeRegistries.TILE_ENTITIES.getValue(tileEntity);
            if (blacklistedTiles.contains(type))
            {
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
    public boolean blacklistTileEntity(TileEntityType tileEntity)
    {
        if (blacklistedTiles.contains(tileEntity))
        {
            LOGGER.warn("TileEntity with id {} is already blacklisted.", tileEntity.getRegistryName());
            return false;
        }
        blacklistedTiles.add(tileEntity);
        return true;
    }

    @Override
    public boolean isBlockBlacklisted(Block block) { return blacklistedBlocks.contains(block); }

    @Override
    public boolean isTileEntityBlacklisted(TileEntityType tileEntityType) { return blacklistedTiles.contains(tileEntityType); }

    @Override
    public boolean registerTorcherinoBlock(Block block)
    {
        if (!torcherinoBlocks.contains(block))
        {
            blacklistBlock(block);
            torcherinoBlocks.add(block);
            return true;
        }
        LOGGER.warn("Torcherino with id {} has already been registered.", block.getRegistryName());
        return false;
    }

    // Do not use
    public void setServerTiers(Map<ResourceLocation, Tier> tiers) { serverTiers = tiers; }

    public ImmutableMap<ResourceLocation, Tier> getTiers() { return ImmutableMap.copyOf(clientTiers); }

    @Override
    public Tier getTier(ResourceLocation name)
    {
        return serverTiers.getOrDefault(name, null);
    }

    @Override
    public ImmutableSet<Block> getTorcherinoBlocks()
    {
        return ImmutableSet.copyOf(torcherinoBlocks);
    }
}
