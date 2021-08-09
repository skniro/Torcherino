package torcherino.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import torcherino.TorcherinoImpl;

/**
 * MIT License
 *
 * Copyright (c) 2021 NinjaPhenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
     * @param blockId The Resource Location of the block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if no block exists or already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean blacklistBlock(ResourceLocation blockId);

    /**
     * @param block The block to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean blacklistBlock(Block block);

    /**
     * @param block The block to check is blacklisted.
     * @return TRUE if blacklisted, FALSE otherwise.
     * @since FABRIC - 1.9.51, FORGE - 8.1.2
     */
    boolean isBlockBlacklisted(Block block);

    /**
     * @param blockEntityTypeId The id of the block entity type to be blacklisted.
     * @return TRUE if added to blacklist, FALSE if already on blacklist.
     * @since FABRIC - 1.9.51, FORGE - 14.0.0
     */
    boolean blacklistBlockEntity(ResourceLocation blockEntityTypeId);

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
