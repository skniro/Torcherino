package torcherino;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.HashSet;

public class Utils
{
	public static final String MOD_ID = "torcherino";
	public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
	public static boolean logPlacement = false;
	public static int randomTickSpeedRate = 1;
	private static HashSet<Block> blacklistedBlocks = new HashSet<>();
	private static HashSet<Class<? extends TileEntity>> blacklistedBlockEntities = new HashSet<>();
	public static HashMap<EntityPlayerMP, Boolean> keyStates = new HashMap<>();

	public static boolean isBlockBlacklisted(Block block){ return blacklistedBlocks.contains(block); }

	public static boolean isTileEntityBlacklisted(TileEntity tileEntity){ return blacklistedBlockEntities.contains(tileEntity.getClass()); }

	public static void blacklistBlock(Block block){ blacklistedBlocks.add(block); }

	public static void blacklistBlocks(Block... blocks){ for (Block block : blocks) blacklistBlock(block); }

	static void blacklistTileEntity(Class<? extends TileEntity> tileEntity){ blacklistedBlockEntities.add(tileEntity); }

	public static void blacklistTileEntity(TileEntity tileEntity){ blacklistedBlockEntities.add(tileEntity.getClass()); }

	public static ResourceLocation getId(String path){ return new ResourceLocation("torcherino", path); }

	static void blacklistString(String string)
	{
		if (string.indexOf(':') == -1)
		{
			try
			{
				Class<?> clazz = Torcherino.class.getClassLoader().loadClass(string);
				if (clazz == null)
				{
					LOGGER.info("Class null: " + string);
					return;
				}
				else if (!TileEntity.class.isAssignableFrom(clazz))
				{
					LOGGER.info("Class not a TileEntity: " + string);
					return;
				}
				LOGGER.info("Blacklisting " + string);
				blacklistTileEntity((Class<? extends TileEntity>) clazz);
			}
			catch (ClassNotFoundException e) { LOGGER.info("Class not found: " + string + ", ignoring"); }
		}
		else
		{
			String[] parts = string.split(":");
			if (parts.length != 2)
			{
				LOGGER.info("Received malformed message: " + string);
				return;
			}
			IForgeRegistry<Block> registry = GameRegistry.findRegistry(Block.class);
			Block block = registry.getValue(new ResourceLocation(parts[0], parts[1]));
			if (block == null)
			{
				LOGGER.info("Could not find block: " + string + ", ignoring");
				return;
			}
			LOGGER.info("Blacklisting " + string);
			blacklistBlock(block);
		}
	}
}
