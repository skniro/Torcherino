package torcherino.api.impl;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.TorcherinoAPI;
import torcherino.api.TorcherinoBlacklistAPI;

/**
 * WARNING: this class will be removed in the future and is not intended to be used.
 *
 * @see torcherino.api.TorcherinoAPI#INSTANCE instead
 */
@Deprecated
public class TorcherinoBlacklistImpl implements TorcherinoBlacklistAPI
{
	public static final TorcherinoBlacklistAPI INSTANCE = new TorcherinoBlacklistImpl();

	private final Logger LOGGER = LogManager.getLogger("torcherino-api");

	@Override public boolean isBlockBlacklisted(Block block)
	{
		return TorcherinoAPI.INSTANCE.isBlockBlacklisted(block);
	}

	@Override public boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType)
	{
		return TorcherinoAPI.INSTANCE.isBlockEntityBlacklisted(blockEntityType);
	}

	@Override public void blacklistBlock(Block block)
	{
		TorcherinoAPI.INSTANCE.blacklistBlock(block);
	}

	@Override public void blacklistBlock(Identifier blockIdentifier)
	{
		if (Registry.BLOCK.containsId(blockIdentifier))
		{
			blacklistBlock(Registry.BLOCK.get(blockIdentifier));
		}
		else
		{
			LOGGER.warn("Could not find a block matching provided identifier: {}.", blockIdentifier);
		}
	}

	@Override public void blacklistBlockEntity(BlockEntityType blockEntityType)
	{
		TorcherinoAPI.INSTANCE.blacklistBlockEntity(blockEntityType);
	}

	@Override public void blacklistBlockEntity(Identifier blockEntityTypeIdentifier)
	{
		if (Registry.BLOCK_ENTITY.containsId(blockEntityTypeIdentifier))
		{
			BlockEntityType blockEntityType = Registry.BLOCK_ENTITY.get(blockEntityTypeIdentifier);
			blacklistBlockEntity(blockEntityType);
		}
		else
		{
			LOGGER.warn("Could not find a block entity type matching provided identifier: {}.", blockEntityTypeIdentifier);
		}
	}
}
