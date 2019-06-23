package torcherino;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraftforge.fml.loading.FMLPaths;
import java.io.*;

public class TorcherinoConfig
{

	public static final TorcherinoConfig INSTANCE = new TorcherinoConfig();
	private final JsonObject defaultConfig;
	private JsonObject config;

	private TorcherinoConfig()
	{
		defaultConfig = new JsonObject();
		defaultConfig.addProperty("log_placement", false);
		defaultConfig.addProperty("random_tick_rate", 1);
		defaultConfig.add("blacklisted_blocks", new JsonArray());
		defaultConfig.add("blacklisted_tiles", new JsonArray());
		JsonObject tiers = new JsonObject();
		JsonObject normalTier = new JsonObject();
		normalTier.addProperty("max_speed", 4);
		normalTier.addProperty("xz_range", 4);
		normalTier.addProperty("y_range", 1);

		JsonObject compressedTier = new JsonObject();
		compressedTier.addProperty("max_speed", 36);
		compressedTier.addProperty("xz_range", 4);
		compressedTier.addProperty("y_range", 1);

		JsonObject doubleCompressedTier = new JsonObject();
		doubleCompressedTier.addProperty("max_speed", 324);
		doubleCompressedTier.addProperty("xz_range", 4);
		doubleCompressedTier.addProperty("y_range", 1);
		tiers.add("normal", normalTier);
		tiers.add("compressed", compressedTier);
		tiers.add("double_compressed", doubleCompressedTier);
		defaultConfig.add("tiers", tiers);
	}

	public void loadConfig()
	{
		File torcherinoConfigFile = new File(FMLPaths.CONFIGDIR.get().toFile(), "torcherino.cfg");
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
		if (torcherinoConfigFile.exists())
		{
			JsonReader reader = null;
			try
			{
				reader = gson.newJsonReader(new FileReader(torcherinoConfigFile));
				config = gson.fromJson(reader, JsonObject.class);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
						Utilities.LOGGER.info("Reader threw IO error when closing.");
					}
				}
			}
		}
		else
		{
			JsonWriter writer = null;
			// write the default config
			try
			{
				if (torcherinoConfigFile.createNewFile())
				{
					writer = gson.newJsonWriter(new FileWriter(torcherinoConfigFile));
					gson.toJson(defaultConfig, writer);
				}
				else
				{
					Utilities.LOGGER.error("Could not create config file");
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (IOException e)
					{
						Utilities.LOGGER.info("Writer threw IO error when closing.");
					}
				}
			}
			config = defaultConfig;
		}
		// finally read our data (from config class member)
		JsonArray blacklistedBlocks = config.getAsJsonArray("blacklisted_blocks");
		Utilities.LOGGER.info("Present? {}, value: {}", blacklistedBlocks != null, blacklistedBlocks != null ? blacklistedBlocks.toString() : "");
		System.exit(0);
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
