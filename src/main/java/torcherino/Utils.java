package torcherino;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.HashSet;

public class Utils
{
    public static final Logger logger = LogManager.getLogger();
    private static HashSet<Block> blacklistedBlocks = new HashSet<>();
    private static HashSet<TileEntityType> blacklistedBlockEntities = new HashSet<>();
    public static HashMap<EntityPlayerMP, Boolean> keyStates = new HashMap<>();

    public static boolean isBlockBlacklisted(Block block)
    {
        return blacklistedBlocks.contains(block);
    }

    public static boolean isTileEntityBlacklisted(TileEntityType tileEntityType)
    {
        return blacklistedBlockEntities.contains(tileEntityType);
    }

    public static void blacklistBlock(Block block)
    {
        blacklistedBlocks.add(block);
    }

    public static void blacklistTileEntity(TileEntityType tileEntityType)
    {
        blacklistedBlockEntities.add(tileEntityType);
    }

    public static void registerItem(ItemBlock itemBlock)
    {
        Torcherino.itemBlocks.add(itemBlock);
    }

    private static ResourceLocation getId(String name)
    {
        return new ResourceLocation("torcherino", name);
    }

    public static ResourceLocation getId(String format, Object... args)
    {
        return getId(String.format(format, args));
    }
}
