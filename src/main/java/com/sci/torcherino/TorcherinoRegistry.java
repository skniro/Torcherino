package com.sci.torcherino;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;

public final class TorcherinoRegistry {
    public static void blacklistBlock(final Block block) {
        TorcherinoRegistry.blacklistedBlocks.add(block);
    }

    public static void blacklistTile(final Class<? extends TileEntity> tile) {
        TorcherinoRegistry.blacklistedTiles.add(tile);
    }

    public static boolean isBlockBlacklisted(final Block block) {
        return TorcherinoRegistry.blacklistedBlocks.contains(block);
    }

    public static boolean isTileBlacklisted(final Class<? extends TileEntity> tile) {
        return TorcherinoRegistry.blacklistedTiles.contains(tile);
    }

    private static Set<Block> blacklistedBlocks = new HashSet<Block>();
    private static Set<Class<? extends TileEntity>> blacklistedTiles = new HashSet<Class<? extends TileEntity>>();
}