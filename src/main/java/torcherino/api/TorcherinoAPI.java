package torcherino.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import torcherino.api.impl.TorcherinoImpl;

/**
 * @author NinjaPhenix
 * @since 1.9.51
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface TorcherinoAPI
{
	/**
	 * The Implementation of the API, you should use this for all methods.
	 * e.g. TorcherinoAPI.INSTANCE.blacklistBlock(Blocks.STONE)
	 */
	TorcherinoAPI INSTANCE = TorcherinoImpl.INSTANCE;

	/**
	 * @return Immutable map of tierID -> tier
	 * @since 1.9.51
	 */
	ImmutableMap<Identifier, Tier> getTiers();

	/**
	 * Returns the tier for the given tierName.
	 *
	 * @param name The tier name to retrieve.
	 * @return The tier or null if it does not exist.
	 * @since 1.9.51
	 */
	Tier getTier(Identifier name);

	/**
	 * @param name     Identifier for the new tier.
	 * @param maxSpeed The max speed blocks of this tier should have.
	 * @param xzRange  The max range horizontally blocks of this tier should have.
	 * @param yRange   The max range vertically blocks of this tier should have.
	 * @return TRUE if the tier was registered, FALSE if tier with same name exists.
	 * @since 1.9.51
	 */
	boolean registerTier(Identifier name, int maxSpeed, int xzRange, int yRange);

	/**
	 * @param blockIdentifier The Identifier of the block to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no block exists or already on blacklist.
	 * @since 1.9.51
	 */
	boolean blacklistBlock(Identifier blockIdentifier);

	/**
	 * @param block The block to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if already on blacklist.
	 * @since 1.9.51
	 */
	boolean blacklistBlock(Block block);

	/**
	 * @param blockEntityIdentifier The Identifier of the block entity to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if no block entity exists or already on blacklist.
	 * @since 1.9.51
	 */
	boolean blacklistBlockEntity(Identifier blockEntityIdentifier);

	/**
	 * @param blockEntity The block entity type to be blacklisted.
	 * @return TRUE if added to blacklist, FALSE if already on blacklist.
	 * @since 1.9.51
	 */
	boolean blacklistBlockEntity(BlockEntityType blockEntity);

	/**
	 * @param block The block to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 * @since 1.9.51
	 */
	boolean isBlockBlacklisted(Block block);

	/**
	 * @param blockEntityType The block entity type to check is blacklisted.
	 * @return TRUE if blacklisted, FALSE otherwise.
	 * @since 1.9.51
	 */
	boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType);

	/**
	 * Use this method so the Torcherino Block Entity includes your block.
	 * @param block The block to register.
	 * @return TRUE if registered and blacklisted, FALSE otherwise.
	 * @since 1.9.51
	 */
	boolean registerTorcherinoBlock(Block block);

	/**
	 * @return A set of all torcherino blocks registered through the API.
	 * @since 1.9.51
	 */
	ImmutableSet<Block> getTorcherinoBlocks();
}
