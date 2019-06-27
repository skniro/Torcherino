package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import torcherino.api.impl.TorcherinoImpl;

public interface TorcherinoAPI
{
	TorcherinoAPI INSTANCE = new TorcherinoImpl();

	/**
	 * @since 8.1.2
	 * @return Immutable map of tierID -> tier
	 */
	ImmutableMap<ResourceLocation, Tier> getTiers();

	/**
	 * @since 8.1.2
	 * @param name Resource Location for the new tier.
	 * @param maxSpeed The max speed blocks of this tier should have.
	 * @param xzRange The max range horizontally blocks of this tier should have.
	 * @param yRange The max range vertically blocks of this tier should have.
	 * @return TRUE if the tier was registered, FALSE if tier with same name exists.
	 */
	boolean registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange);

	/**
	 * @since 8.1.2
	 * @param block The Resource Location of the block to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no block exists or already on blacklist.
	 */
	boolean blacklistBlock(ResourceLocation block);

	/**
	 * @since 8.1.2
	 * @param tileEntity The Resource Location of the tile entity to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no tile entity exists or already on blacklist.
	 */
	boolean blacklistTileEntity(ResourceLocation tileEntity);

	/**
	 * @since 8.1.2
	 * @param block The block to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 */
	boolean isBlockBlacklisted(Block block);

	/**
	 * @since 8.1.2
	 * @param tileEntityType The tile entity type to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 */
	boolean isTileEntityBlacklisted(TileEntityType tileEntityType);

}
