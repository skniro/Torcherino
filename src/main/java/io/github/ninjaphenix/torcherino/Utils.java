package io.github.ninjaphenix.torcherino;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class Utils
{
    static final String MOD_ID = "torcherino";
    private static final String MOD_NAME = "test";
    private static final Logger MOD_LOGGER = LogManager.getLogger(MOD_NAME);
    static void info(String msg)
    {
        MOD_LOGGER.info("Torcherino: "+msg);
    }

    // Blacklist
    public static void blacklistString(String string)
    {
        if (string.indexOf(':') == -1)
        {
            try
            {
                Class<?> clazz = Torcherino.class.getClassLoader().loadClass(string);
                if(clazz == null)
                {
                    info("Class null: " + string);
                    return;
                }
                else if(!TileEntity.class.isAssignableFrom(clazz))
                {
                    info("Class not a TileEntity: " + string);
                    return;
                }
                blacklistTile((Class<? extends TileEntity>) clazz);
            }
            catch(ClassNotFoundException e)
            {
                info("Class not found: " + string + ", ignoring");
            }
        }
        else
        {
            String[] parts = string.split(":");
            if(parts.length != 2)
            {
                info("Received malformed message: " + string);
                return;
            }
            Block block = Block.REGISTRY.get(new ResourceLocation(parts[0], parts[1]));
            if(block == Blocks.AIR)
            {
                info("Could not find block: " + string + ", ignoring");
                return;
            }
            info("Blacklisting block: " + block);
            blacklistBlock(block);
        }
    }
    static void blacklistBlock(Block block)
    {
        blacklistedBlocks.add(block);
    }
    static void blacklistTile(Class<? extends TileEntity> tile)
    {
        blacklistedTiles.add(tile);
    }
    protected static boolean isBlockBlacklisted(Block block)
    {
        return blacklistedBlocks.contains(block);
    }
    protected static boolean isTileBlacklisted(Class<? extends TileEntity> tile)
    {
        return blacklistedTiles.contains(tile);
    }
    private static Set<Block> blacklistedBlocks = new HashSet<>();
    private static Set<Class<? extends TileEntity>> blacklistedTiles = new HashSet<>();


}
