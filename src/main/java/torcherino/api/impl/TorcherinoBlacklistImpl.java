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

	@Override public void blacklistBlockEntity(BlockEntityType blockEntityType)
	{
		blacklistedBlockEntities.add(blockEntityType);
	}

	@Override public void blacklistIdentifier(Identifier identifier)
	{
		Block block = Registry.BLOCK.get(identifier);
		if (block != Blocks.AIR)
		{
			blacklistBlock(block);
		}
		else
		{
			BlockEntityType blockEntityType = Registry.BLOCK_ENTITY.get(identifier);
			if (blockEntityType != BlockEntityType.FURNACE || identifier.equals(new Identifier("minecraft", "furnace")))
			{
				blacklistBlockEntity(blockEntityType);
			}
			else
			{
				LOGGER.warn("Could not find a block or block entity type matching provided identifier: {}.", identifier);
			}
		}
	}
}
