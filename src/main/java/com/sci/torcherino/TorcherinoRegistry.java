package com.sci.torcherino;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public final class TorcherinoRegistry {
	public static void blacklistString(final String string) {
		if (string.indexOf(':') == -1) {
			try {
		        final Class<?> clazz = Torcherino.instance().getClass().getClassLoader().loadClass(string);
		        if (clazz == null) {
		            System.out.println("Class null: " + string);
		            return;
		        }
		        if (!TileEntity.class.isAssignableFrom(clazz)) {
		            System.out.println("Class not a TileEntity: " + string);
		            return;
		        }
		        blacklistTile((Class<? extends TileEntity>) clazz);
		    } catch (final ClassNotFoundException e) {
		        System.out.println("Class not found: " + string + ", ignoring");
		    }
		}else {
			final String[] parts = string.split(":");

		    if (parts.length != 2) {
		        System.out.println("Received malformed message: " + string);
		        return;
		    }

		    final Block block = Block.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));

		    if (block == null) {
		        System.out.println("Could not find block: " + string + ", ignoring");
		        return;
		    }

		    System.out.println("Blacklisting block: " + block.getUnlocalizedName());

		    blacklistBlock(block);
		}
	}
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