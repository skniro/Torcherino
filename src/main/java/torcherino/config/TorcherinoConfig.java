package torcherino.config;

import blue.endless.jankson.Comment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;

public class TorcherinoConfig
{
	public static TorcherinoConfig INSTANCE;

	@Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096") public final int random_tick_rate;

	@Comment("Log torcherino placement (Intended for server use)") public final boolean log_placement;

	@Comment("\nAdd a block by resource location to the blacklist.\nExamples: minecraft:dirt, minecraft:furnace") public final ResourceLocation[] blacklisted_blocks;

	@Comment("\nAdd a tile entity by resource location to the blacklist.\nExamples: minecraft:furnace, minecraft:mob_spawner") public final ResourceLocation[] blacklisted_tiles;

	@Comment("\nAllows new custom torcherino tiers to be added.\nThis also allows for each tier to have their own max MAX_SPEED and ranges.") public final Tier[] tiers;

	public TorcherinoConfig()
	{
		random_tick_rate = 1;
		log_placement = FMLLoader.getDist().isDedicatedServer();
		blacklisted_blocks = new ResourceLocation[]{};
		blacklisted_tiles =  new ResourceLocation[]{};
		tiers = new Tier[]{
				new Tier("normal", 4, 4, 1),
				new Tier("compressed", 36, 4, 1),
				new Tier("double_compressed", 324, 4, 1)
		};
	}

	private class Tier
	{
		final String NAME;
		final int MAX_SPEED;
		final int XZ_RANGE;
		final int Y_RANGE;

		Tier(String name, int max_speed, int xz_range, int y_range)
		{
			this.NAME = name;
			this.MAX_SPEED = max_speed;
			this.XZ_RANGE = xz_range;
			this.Y_RANGE = y_range;
		}
	}
}