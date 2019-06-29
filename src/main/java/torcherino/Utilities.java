package torcherino;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*

	Todo: consider removing this class. / Merging into torcherino main class.

 */
public class Utilities
{
	public static final Logger LOGGER = LogManager.getLogger(Torcherino.class);
	public static final String MOD_ID = "torcherino";

	public static ResourceLocation resloc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}
