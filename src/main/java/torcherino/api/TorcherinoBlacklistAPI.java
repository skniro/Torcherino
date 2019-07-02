package torcherino.api;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import torcherino.api.impl.TorcherinoBlacklistImpl;

/**
 * @author NinjaPhenix
 * @since 1.8.49
 * @deprecated Warning this interface will be removed in 1.15.x see TorcherinoAPI interface instead.
 */
@Deprecated
@SuppressWarnings({ "UnusedReturnValue", "unused", "DeprecatedIsStillUsed" })
public interface TorcherinoBlacklistAPI
{
    /**
     * The Implementation of the API, you should use this for all methods. e.g. TorcherinoBlacklistAPI.INSTANCE.blacklistBlock(Blocks.STONE)
     */
    TorcherinoBlacklistAPI INSTANCE = TorcherinoBlacklistImpl.INSTANCE;

    /**
     * Checks if a Block is blacklisted.
     *
     * @param block The Block to check is blacklisted.
     * @return boolean primitive, true for block is blacklisted, false otherwise.
     * @since 1.8.49
     */
    boolean isBlockBlacklisted(Block block);

    /**
     * Checks if a BlockEntityType is blacklisted.
     *
     * @param blockEntityType The BlockEntityType to check is blacklisted.
     * @return boolean primitive, true for block entity type is blacklisted, false otherwise.
     * @since 1.8.49
     */
    boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType);

    /**
     * Blacklists a Block from being ticked.
     *
     * @param block The block to blacklist.
     * @since 1.8.49
     */
    void blacklistBlock(Block block);

    /**
     * Same as blacklistBlock(Block) however will attempt to find the block from Registry.BLOCK by identifier.
     *
     * @param blockIdentifier The Identifier for the Block to be blacklisted.
     * @since 1.8.49
     */
    void blacklistBlock(Identifier blockIdentifier);

    /**
     * Blacklists a BlockEntityType from being ticked.
     *
     * @param blockEntityType The Block Entity Type to blacklist.
     * @since 1.8.49
     */
    void blacklistBlockEntity(BlockEntityType blockEntityType);

    /**
     * Same as blacklistBlock(Block) however will attempt to find the block from Registry.BLOCK_ENTITY by identifier.
     *
     * @param blockEntityTypeIdentifier The identifier for the BlockEntityType to be blacklisted.
     * @since 1.8.49
     */
    void blacklistBlockEntity(Identifier blockEntityTypeIdentifier);
}
