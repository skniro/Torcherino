package torcherino;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Utils
{
    public static final Logger logger = LogManager.getLogger();
    private static Set<Block> blacklistedBlocks = new HashSet<>();
    private static Set<BlockEntityType> blacklistedBlockEntities = new HashSet<>();
    public static HashMap<PlayerEntity, Boolean> keyStates = new HashMap<>();

    public static boolean isBlockBlacklisted(Block block)
    {
        return blacklistedBlocks.contains(block);
    }

    public static boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType)
    {
        return blacklistedBlockEntities.contains(blockEntityType);
    }

    public static void blacklistBlock(Block block)
    {
        blacklistedBlocks.add(block);
    }

    public static void blacklistBlockEntity(BlockEntityType blockEntityType)
    {
        blacklistedBlockEntities.add(blockEntityType);
    }

    public static Identifier getId(String name)
    {
        return new Identifier("torcherino", name);
    }

    public static Identifier getId(String format, Object... args)
    {
        return getId(String.format(format, args));
    }

}
