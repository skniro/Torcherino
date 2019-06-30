package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import torcherino.api.impl.TorcherinoImpl;

@SuppressWarnings("UnusedReturnValue")
public interface TorcherinoAPI
{
	TorcherinoAPI INSTANCE = new TorcherinoImpl();

	/**
	 * @return Immutable map of tierID -> tier
	 * @since 8.1.2
	 */
	ImmutableMap<net.minecraft.util.ResourceLocation, Tier> getTiers();

	/**
	 * Returns the tier for the given tierName.
	 *
	 * @param name The tier name to retrieve.
	 * @return The tier or null if it does not exist.
	 * @since 8.1.2
	 */
	Tier getTier(net.minecraft.util.ResourceLocation name);

	/**
	 * @param name     Resource Location for the new tier.
	 * @param maxSpeed The max speed blocks of this tier should have.
	 * @param xzRange  The max range horizontally blocks of this tier should have.
	 * @param yRange   The max range vertically blocks of this tier should have.
	 * @return TRUE if the tier was registered, FALSE if tier with same name exists.
	 * @since 8.1.2
	 */
	boolean registerTier(net.minecraft.util.ResourceLocation name, int maxSpeed, int xzRange, int yRange);

	/**
	 * @param block The Resource Location of the block to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no block exists or already on blacklist.
	 * @since 8.1.2
	 */
	boolean blacklistBlock(net.minecraft.util.ResourceLocation block);

	/**
	 * @param block The block to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if already on blacklist.
	 * @since 8.1.2
	 */
	boolean blacklistBlock(Block block);

	/**
	 * @param tileEntity The Resource Location of the tile entity to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no tile entity exists or already on blacklist.
	 * @since 8.1.2
	 */
	boolean blacklistTileEntity(net.minecraft.util.ResourceLocation tileEntity);

	/**
	 * @param tileEntity The tile entity type to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if already on blacklist.
	 * @since 8.1.2
	 */
	boolean blacklistTileEntity(TileEntityType tileEntity);

	/**
	 * @param block The block to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 * @since 8.1.2
	 */
	boolean isBlockBlacklisted(Block block);

	/**
	 * @param tileEntityType The tile entity type to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 * @since 8.1.2
	 */
	boolean isTileEntityBlacklisted(TileEntityType tileEntityType);

	/**
	 * Blacklists and adds the torcherino to a list.
	 * Will be used in 1.14.x to assign the torcherino tile entity to blocks.
	 *
	 * @param block The torcherino / lanterino block
	 * @return TRUE if registered and blacklisted, FALSE otherwise.
	 * @since 8.1.2
	 */
	boolean registerTorcherinoBlock(Block block);
}
