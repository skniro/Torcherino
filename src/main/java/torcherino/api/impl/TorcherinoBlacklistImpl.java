package torcherino.api.impl;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoBlacklistAPI;
import java.util.HashSet;

/**
 * DO NOT USE THIS CLASS DIRECTLY. Use TorcherinoBlacklistAPI.INSTANCE instead.
 * Why? anything in this class is subject to change where as anything in the API won't
 * be removed without significant warning time. e.g. a minecraft version update or major mod update.
 */
public class TorcherinoBlacklistImpl implements TorcherinoBlacklistAPI
{
	public static final TorcherinoBlacklistAPI INSTANCE = new TorcherinoBlacklistImpl();

	private final Logger LOGGER = LogManager.getLogger("torcherino-api");
	private final HashSet<Block> blacklistedBlocks;
	private final HashSet<BlockEntityType> blacklistedBlockEntities;

	private TorcherinoBlacklistImpl()
	{
		blacklistedBlocks = new HashSet<>();
		blacklistedBlockEntities = new HashSet<>();
	}

	@Override public boolean isBlockBlacklisted(Block block)
	{
		return blacklistedBlocks.contains(block);
	}

	@Override public boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType)
	{
		return blacklistedBlockEntities.contains(blockEntityType);
	}

	@Override public void blacklistBlock(Block block)
	{
		blacklistedBlocks.add(block);
	}

	@Override public void blacklistBlock(Identifier blockIdentifier)
	{
		Block block = Registry.BLOCK.get(blockIdentifier);
		// why would someone want to blacklist air? Let's let them anyways.
		if (block != Blocks.AIR || blockIdentifier.equals(Registry.BLOCK.getId(Blocks.AIR)))
			blacklistBlock(block);
		else
			LOGGER.warn("Could not find a block matching provided identifier: {}.", blockIdentifier);

	}

	@Override public void blacklistBlockEntity(BlockEntityType blockEntityType)
	{
		blacklistedBlockEntities.add(blockEntityType);
	}

	@Override public void blacklistBlockEntity(Identifier blockEntityTypeIdentifier)
	{
		BlockEntityType blockEntityType = Registry.BLOCK_ENTITY.get(blockEntityTypeIdentifier);
		if (blockEntityType != BlockEntityType.FURNACE || blockEntityTypeIdentifier.equals(Registry.BLOCK_ENTITY.getId(BlockEntityType.FURNACE)))
			blacklistBlockEntity(blockEntityType);
		else
			LOGGER.warn("Could not find a block entity type matching provided identifier: {}.", blockEntityTypeIdentifier);
	}
}
