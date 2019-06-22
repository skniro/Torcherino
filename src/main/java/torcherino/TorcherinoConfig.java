package torcherino;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TorcherinoConfig
{

	public static final TorcherinoConfig INSTANCE = new TorcherinoConfig();

	public void loadConfig()
	{
		File torcherinoConfigFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "torcherino.cfg");
		Gson gson = new GsonBuilder().create();
		if (torcherinoConfigFile.exists())
		{
			try
			{
				JsonReader reader = gson.newJsonReader(new FileReader(torcherinoConfigFile));
				JsonObject config = (JsonObject) Streams.parse(reader);
				JsonArray blacklistedBlocks = config.getAsJsonArray("blacklistedBlocks");
				Utilities.LOGGER.info("Present? {}, value: {}", blacklistedBlocks != null, blacklistedBlocks != null ? blacklistedBlocks.toString() : "");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			// write the default config
			Utilities.LOGGER.info("No config found...");
		}

		// Load
		//Utils.logPlacement = config.getConfigData().get("logPlacement");
		//Utils.randomTickSpeedRate = MathHelper.clamp(config.getConfigData().get("randomTickSpeedRate"), 1, 4096);
		//List<String> blocks = configData.get("blacklistedBlocks");
		////for (String block : blocks) Utils.blacklistString(block);
		//List<String> tiles = configData.get("blacklistedTiles");
		////for (String tile : tiles) Utils.blacklistString(tile);
		//List<String> tiers = configData.get("tiers");
		//for (String tier : tiers)
		//{
		//	String[] values = tier.split(":");
		//	if (values.length == 0) Utilities.LOGGER.error("A tier was defined with no data.");
		//	else
		//	{
		//		String name = values[0];
		//		try
		//		{
		//			int xz_range = (Integer.valueOf(values[1]) - 1) / 2;
		//			int y_range = (Integer.valueOf(values[2]) - 1) / 2;
		//			int max_speed = Integer.valueOf(values[3]);
		//			TorcherinoTiers.INSTANCE.registerTier(Utilities.resloc(name), max_speed, xz_range, y_range);
		//		}
		//		catch (NumberFormatException exception)
		//		{
		//			Utilities.LOGGER.error("One of tier {}'s value is bad", name);
		//		}
		//	}
		//}
	}
	//public static class COMMON
	//{
	//	final ForgeConfigSpec.BooleanValue logPlacement;
	//	final ForgeConfigSpec.ConfigValue<List<String>> blacklistedBlocks;
	//	final ForgeConfigSpec.ConfigValue<List<String>> blacklistedTiles;
	//	final ForgeConfigSpec.ConfigValue<List<String>> tiers;
	//	final ForgeConfigSpec.ConfigValue<Integer> randomTickSpeedRate;
	//
	//	COMMON(ForgeConfigSpec.Builder builder)
	//	{
	//		blacklistedBlocks = builder.comment("Add block by resource location to the blacklist\nExample: [\"minecraft:furnace\", \"minecraft:grass_block\"]").translation("torcherino.configgui.blacklistedblocks").define("blacklistedBlocks", new ArrayList<>());
	//		blacklistedTiles = builder.comment("Add tileentity by class path to the blacklist\nExample: [\"net.minecraft.tileentity.TileEntityFurnace\"]").translation("torcherino.configgui.blacklistedtiles").define("blacklistedTiles", new ArrayList<>());
	//		tiers = builder.comment("Allows for custom torcherino, lanterino types.\nFormat: tier name, x/z length, y length, max speed\nAll ranges must be odd integers.").define("tiers", Lists.asList("normal:9:3:400", new String[]{"compressed:9:3:3600", "double_compressed:9:3:32400"}));
	//		logPlacement = builder.comment("Log torcherino placement (Intended for server use)").translation("torcherino.configgui.logplacement").define("logPlacement", FMLEnvironment.dist.isDedicatedServer());
	//		randomTickSpeedRate = builder.comment("Defines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096").translation("torcherino.configgui.randomtickspeedrate").define("randomTickSpeedRate", 1);
	//	}
	//}
}
