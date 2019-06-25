package torcherino.config;

import blue.endless.jankson.Comment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import torcherino.config.annotations.ConfigFile;
import java.util.ArrayList;
import java.util.List;

@ConfigFile(name = "Torcherino")
public class TorcherinoConfig
{
	public static TorcherinoConfig INSTANCE;
	@Comment("Defines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096")
	public int random_tick_rate = 1;

	@Comment("Log torcherino placement (Intended for server use)")
	public boolean log_placement = FMLLoader.getDist().isDedicatedServer();

	@Comment("Add a block by resource location to the blacklist.\nExamples: minecraft:dirt, minecraft:furnace")
	public List<ResourceLocation> blacklisted_blocks = new ArrayList<>();

	@Comment("Add a tile entity by resource location to the blacklist.\nExamples: minecraft:furnace, minecraft:mob_spawner")
	public List<ResourceLocation> blacklisted_tiles = new ArrayList<>();
}