package com.sci.torcherino.config;

import com.sci.torcherino.Torcherino;
import com.sci.torcherino.TorcherinoRegistry;

import net.minecraftforge.common.config.Configuration;

public class ConfigR {

	public static boolean logPlacement;
	public static boolean overPoweredRecipe;
	public static boolean compressedTorcherino;
	public static boolean doubleCompressedTorcherino;
	private static String[] blacklistedBlocks;
	private static String[] blacklistedTiles;

	public static void init(Configuration cfg) {
		try {
	        cfg.load();
	
	        logPlacement = cfg.getBoolean("logPlacement", "general", false, "(For Server Owners) Is it logged when someone places a Torcherino?");
	        overPoweredRecipe = cfg.getBoolean("overPoweredRecipe", "general", true, "Is the recipe for Torcherino extremely OP?");
	        compressedTorcherino = cfg.getBoolean("compressedTorcherino", "general", false, "Is the recipe for the Compressed Torcherino enabled?");
	        doubleCompressedTorcherino = cfg.getBoolean("doubleCompressedTorcherino", "general", false, "Is the recipe for the Double Compressed Torcherino enabled? Only takes effect if Compressed Torcherinos are enabled.");
	
	        blacklistedBlocks = cfg.getStringList("blacklistedBlocks", "blacklist", new String[]{}, "modid:unlocalized");
	        blacklistedTiles = cfg.getStringList("blacklistedTiles", "blacklist", new String[]{}, "Fully qualified class name");
	    } finally {
	        if (cfg.hasChanged())
	            cfg.save();
	    }
	}
	
	public static void postInit() {
		for (final String block : blacklistedBlocks)
            TorcherinoRegistry.blacklistString(block);
        for (final String tile : blacklistedTiles)
        	TorcherinoRegistry.blacklistString(tile);
	}
}
