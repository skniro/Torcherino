package torcherino.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class Config
{
	@Value(name = "random_tick_rate", comment = {"Defines how much faster randoms ticks are applied compared to what they should be.", "Valid Range: 1 to 4096"}) private int randomTickRate = 1;

	@Value(name = "log_placement", comment = {"Log torcherino placement (Intended for server use)"}) private boolean logPlacement = FMLEnvironment.dist.isDedicatedServer();

	@Value(name="blacklisted_blocks", comment={"Add block by resource location to the blacklist", "Examples: minecraft:dirt, minecraft:furnace"}) private ResourceLocation[] blacklistedBlocks;

	@Value(name="blacklisted_tiles", comment={"Add tile entity by resource location to the blacklist", "Examples: minecraft:furnace, minecraft:mob_spawner"}) private ResourceLocation[] blacklistedTiles;
}
