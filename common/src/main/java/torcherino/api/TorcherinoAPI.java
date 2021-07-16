package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import torcherino.TorcherinoImpl;

@SuppressWarnings("UnusedReturnValue")
public interface TorcherinoAPI {
    TorcherinoAPI INSTANCE = new TorcherinoImpl();

    /**
     * @return Immutable map of tierID -> tier
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    ImmutableMap<ResourceLocation, Tier> getTiers();

    /**
     * Returns the tier for the given tierName.
     *
     * @param name The tier name to retrieve.
     * @return The tier or null if it does not exist.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    Tier getTier(ResourceLocation name);

    /**
     * @param name     Resource Location for the new tier.
     * @param maxSpeed The max speed blocks of this tier should have.
     * @param xzRange  The max range horizontally blocks of this tier should have.
     * @param yRange   The max range vertically blocks of this tier should have.
     * @return TRUE if the tier was registered, FALSE if tier with same name exists.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean registerTier(ResourceLocation name, int maxSpeed, int xzRange, int yRange);

    /**
     * @param block The Resource Location of the block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if no block exists or already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean blacklistBlock(ResourceLocation block);

    /**
     * @param block The block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean blacklistBlock(Block block);

    /**
     * @param blockEntityId The id of the block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if no block entity type exists or already on blacklist.
     * @deprecated Removed in 15.0.0
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    @Deprecated
    boolean blacklistTileEntity(ResourceLocation blockEntityId);

    /**
     * @param blockEntityType The block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @deprecated Removed in 15.0.0
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    @Deprecated
    boolean blacklistTileEntity(BlockEntityType<? extends BlockEntity> blockEntityType);

    /**
     * @param block The block to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean isBlockBlacklisted(Block block);

    /**
     * @param blockEntityType The block entity type to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @deprecated Removed in 15.0.0
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    @Deprecated
    boolean isTileEntityBlacklisted(BlockEntityType<? extends BlockEntity> blockEntityType);

    // todo: update version numbers
    /**
     * @param blockEntityId The id of the block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 14.0.0
     */
    boolean blacklistBlockEntity(ResourceLocation blockEntityId);

    /**
     * @param blockEntityType The block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 14.0.0
     */
    boolean blacklistBlockEntity(BlockEntityType<?> blockEntityType);

    /**
     * @param blockEntityType The block entity type to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @since FABRIC - 1.9.51, FORGE - 14.0.0
     */
    boolean isBlockEntityBlacklisted(BlockEntityType<?> blockEntityType);
}
