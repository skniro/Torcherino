package torcherino;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.List;

public class TorcherinoConfig
{
	public static class COMMON
	{
		//final ForgeConfigSpec.BooleanValue compressedEnabled;
		//final ForgeConfigSpec.BooleanValue doubleCompressedEnabled;
		final ForgeConfigSpec.BooleanValue logPlacement;
		final ForgeConfigSpec.ConfigValue<List<String>> blacklistedBlocks;
		final ForgeConfigSpec.ConfigValue<List<String>> blacklistedTiles;

		COMMON(ForgeConfigSpec.Builder builder)
		{
			//compressedEnabled = builder.comment("Set this to true to enable compressed torcherino and lanterinos")
			//		.translation("torcherino.configgui.compressedenabled")
			//		.define("enableCompressed", false);
			//doubleCompressedEnabled = builder.comment("Set this to true to enable double compressed torcherino and lanterinos")
			//		.translation("torcherino.configgui.doublecompressedenabled")
			//		.define("enableDoubleCompressed", false);
			blacklistedBlocks = builder.comment("Add block by resource location to the blacklist\nExample: [\"minecraft:furnace\", \"minecraft:grass_block\"]")
					.translation("torcherino.configgui.blacklistedblocks")
					.define("blacklistedBlocks", new ArrayList<>());
			blacklistedTiles = builder.comment("Add tileentity by class path to the blacklist\nExample: [\"net.minecraft.tileentity.TileEntityFurnace\"]")
					.translation("torcherino.configgui.blacklistedtiles")
					.define("blacklistedTiles", new ArrayList<>());
			logPlacement = builder.comment("Log torcherino placement (Intended for server use)")
					.translation("torcherino.configgui.logplacement")
					.define("logPlacement", FMLEnvironment.dist.isDedicatedServer());
		}
	}

	private static final Pair<COMMON, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(COMMON::new);
	static final ForgeConfigSpec commonSpec = specPair.getRight();
	private static final COMMON common = specPair.getLeft();

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent)
	{
		ModConfig config = configEvent.getConfig();
		Utils.logPlacement = config.getConfigData().get("logPlacement");
		Utils.LOGGER.info("Torcherino config has been loaded from {}", config.getFileName());
		List<String> blocks = config.getConfigData().get("blacklistedBlocks");
		for(String block : blocks) Utils.blacklistString(block);
		List<String> tiles = config.getConfigData().get("blacklistedTiles");
		for(String tile : tiles) Utils.blacklistString(tile);
	}
}
