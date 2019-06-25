package torcherino.config;

import blue.endless.jankson.Comment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;

public class TorcherinoConfig
{
	public static TorcherinoConfig INSTANCE;

	@Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096") public int random_tick_rate = 1;

	@Comment("Log torcherino placement (Intended for server use)") public boolean log_placement = FMLLoader.getDist().isDedicatedServer();

	@Comment("\nAdd a block by resource location to the blacklist.\nExamples: minecraft:dirt, minecraft:furnace") public ResourceLocation[] blacklisted_blocks = new ResourceLocation[]{};

	@Comment("\nAdd a tile entity by resource location to the blacklist.\nExamples: minecraft:furnace, minecraft:mob_spawner") public ResourceLocation[] blacklisted_tiles = new ResourceLocation[]{};
}