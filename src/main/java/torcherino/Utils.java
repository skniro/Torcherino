package torcherino;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashSet;

public class Utils
{
	public static final Logger LOGGER = LogManager.getLogger();
	private static HashSet<Block> blacklistedBlocks = new HashSet<>();
	private static HashSet<BlockEntityType> blacklistedBlockEntities = new HashSet<>();

	public static boolean isBlockBlacklisted(Block block){ return blacklistedBlocks.contains(block); }

	public static boolean isBlockEntityBlacklisted(BlockEntityType blockEntityType){ return blacklistedBlockEntities.contains(blockEntityType); }

	public static void blacklistBlock(Block block){ blacklistedBlocks.add(block); }

	public static void blacklistBlockEntity(BlockEntityType blockEntityType){ blacklistedBlockEntities.add(blockEntityType); }

	public static Identifier getId(String name){ return new Identifier("torcherino", name); }

	public static Identifier getId(String format, Object... args){ return getId(String.format(format, args)); }

	public static void blacklistString(String string)
	{
		if (string.indexOf(":") > 0)
		{
			// It's either a block identifier or block entity type identifier.
			Identifier identifier = new Identifier(string);
			Block block = Registry.BLOCK.get(identifier);
			if (block != Blocks.AIR)
			{
				blacklistBlock(block);
			}
			else
			{
				BlockEntityType blockEntityType = Registry.BLOCK_ENTITY.get(identifier);
				if(blockEntityType != BlockEntityType.FURNACE || identifier.equals(new Identifier("minecraft", "furnace")))
				{
					blacklistBlockEntity(blockEntityType);
				}
				else
				{
					LOGGER.warn("Could not find a block or block entity type matching provided identifier: {}.", string);
				}
			}
		}
		else
		{
			LOGGER.warn("Malformed blacklist string provided, must be a block or block entity identifier e.g. minecraft:dirt");
		}
	}
}
