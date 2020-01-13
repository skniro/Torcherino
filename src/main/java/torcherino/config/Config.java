package torcherino.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config
{
    public static Config INSTANCE;

    @Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096")
    public final int random_tick_rate = 4;

    @Comment("Log torcherino placement (Intended for server use)") public final boolean log_placement = FMLLoader.getDist().isDedicatedServer();

    @Comment("\nAdd a block by resource location to the blacklist.\nExamples: \"minecraft:dirt\", \"minecraft:furnace\"")
    public final ResourceLocation[] blacklisted_blocks = new ResourceLocation[]{};

    @Comment("\nAdd a tile entity by resource location to the blacklist.\nExamples: \"minecraft:furnace\", \"minecraft:mob_spawner\"")
    public final ResourceLocation[] blacklisted_tiles = new ResourceLocation[]{};

    @Comment("\nAllows new custom torcherino tiers to be added.\nThis also allows for each tier to have their own max max_speed and ranges.")
    public final Tier[] tiers = new Tier[]{ new Tier("normal", 4, 4, 1), new Tier("compressed", 36, 4, 1), new Tier("double_compressed", 324, 4, 1) };

    @SuppressWarnings("ConstantConditions")
    public static void initialise()
    {
        ConfigManager.getMarshaller().registerSerializer(ResourceLocation.class, JsonPrimitive::new);
        ConfigManager.getMarshaller().register(ResourceLocation.class,
                (it) -> (it instanceof String) ? new ResourceLocation((String) it) : new ResourceLocation(it.toString()));
        ConfigManager.getMarshaller().registerTypeAdapter(Tier.class, (it) ->
        {
            String name = it.get(String.class, "name");
            Integer max_speed = it.get(Integer.class, "max_speed");
            Integer xz_range = it.get(Integer.class, "xz_range");
            Integer y_range = it.get(Integer.class, "y_range");
            return new Tier(name, max_speed < 1 ? 1 : max_speed, xz_range < 0 ? 0 : xz_range, y_range < 0 ? 0 : y_range);
        });
        Path sci4meDirectory = FMLPaths.CONFIGDIR.get().resolve("sci4me");
        if (!sci4meDirectory.toFile().exists())
        {
            try
            {
                Files.createDirectory(sci4meDirectory);
                INSTANCE = ConfigManager.loadConfig(Config.class, sci4meDirectory.resolve("Torcherino.cfg").toFile());
            }
            catch (IOException e)
            {
                Torcherino.LOGGER.error("Failed to create sci4me folder, config won't be saved.");
                INSTANCE = new Config();
            }
        }
        else
        {
            INSTANCE = ConfigManager.loadConfig(Config.class, sci4meDirectory.resolve("Torcherino.cfg").toFile());
        }
        INSTANCE.onConfigLoaded();
    }

    private void onConfigLoaded()
    {
        for (Tier tier : tiers) TorcherinoAPI.INSTANCE.registerTier(Torcherino.resloc(tier.name), tier.max_speed, tier.xz_range, tier.y_range);
        for (ResourceLocation block : blacklisted_blocks) TorcherinoAPI.INSTANCE.blacklistBlock(block);
        for (ResourceLocation tile : blacklisted_tiles) TorcherinoAPI.INSTANCE.blacklistTileEntity(tile);
    }

    private static class Tier
    {
        final String name;
        final int max_speed, xz_range, y_range;

        Tier(String name, int max_speed, int xz_range, int y_range)
        {
            this.name = name;
            this.max_speed = max_speed;
            this.xz_range = xz_range;
            this.y_range = y_range;
        }
    }
}