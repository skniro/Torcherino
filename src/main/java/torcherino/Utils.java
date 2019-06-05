package torcherino;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils
{
	public static final Logger LOGGER = LogManager.getLogger();

	public static Identifier getId(String name){ return new Identifier("torcherino", name); }

	public static Identifier getId(String format, Object... args){ return getId(String.format(format, args)); }
}
