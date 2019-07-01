package torcherino.api.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import java.util.HashSet;

/**
 * DO NOT USE THIS CLASS DIRECTLY. Use TorcherinoAPI.INSTANCE instead.
 * Why? anything in this class is subject to change where as anything in the API won't
 * be removed without significant warning time. e.g. a minecraft version update or major mod update.
 */
public class TorcherinoImpl implements TorcherinoAPI
{
	public static final TorcherinoImpl INSTANCE = new TorcherinoImpl();

	private final Logger LOGGER = LogManager.getLogger("torcherino-api");
	private final HashSet<Block> blacklistedBlocks;
	private final HashSet<BlockEntityType> blacklistedBlockEntities;

	private TorcherinoImpl()
	{
		blacklistedBlocks = new HashSet<>();
		blacklistedBlockEntities = new HashSet<>();
	}

	@Override public ImmutableMap<Identifier, Tier> getTiers()
	{
		return null;
	}

	@Override public Tier getTier(Identifier name)
	{
		return null;
	}

	@Override public boolean registerTier(Identifier name, int maxSpeed, int xzRange, int yRange)
	{
		return false;
	}

	@Override public boolean blacklistBlock(Identifier blockIdentifier)
	{
		return false;
	}

	@Override public boolean blacklistBlock(Block block)
	{
		return false;
	}

	@Override public boolean blacklistBlockEntity(Identifier blockEntityIdentifier)
	{
		return false;
	}

	@Override public boolean blacklistBlockEntity(BlockEntityType blockEntity)
	{
		return false;
	}

	@Override public boolean isBlockBlacklisted(Block block)
	{
		return false;
	}

	@Override public boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType)
	{
		return false;
	}

	@Override public boolean registerTorcherinoBlock(Block block)
	{
		return false;
	}

	@Override public ImmutableSet<Block> getTorcherinoBlocks()
	{
		return null;
	}
}
