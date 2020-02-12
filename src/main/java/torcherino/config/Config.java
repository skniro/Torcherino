package torcherino.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import torcherino.Torcherino;
import torcherino.api.TorcherinoAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config
{
    public static Config INSTANCE;

    @Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096")
    public final int random_tick_rate = 1;

    @Comment("Log torcherino placement (Intended for server use)") public final boolean log_placement = FabricLoader.getInstance().getEnvironmentType() ==
            EnvType.SERVER;

    @Comment("\nAdd a block by identifier to the blacklist.\nExamples: \"minecraft:dirt\", \"minecraft:furnace\"")
    private final Identifier[] blacklisted_blocks = new Identifier[]{};

    @Comment("\nAdd a block entity by identifier to the blacklist.\nExamples: \"minecraft:furnace\", \"minecraft:mob_spawner\"")
    private final Identifier[] blacklisted_blockentities = new Identifier[]{};

    @Comment("\nAllows new custom torcherino tiers to be added.\nThis also allows for each tier to have their own max max_speed and ranges.")
    private final Tier[] tiers = new Tier[]{ new Tier("normal", 4, 4, 1), new Tier("compressed", 36, 4, 1), new Tier("double_compressed", 324, 4, 1) };

    @Comment("\nWhen set to ONLINE, Torcherino's only run if the player is currently online\nIf set to RESTART then Torcherino's will run for anyone who has logged in since the server started.\nAnything else then Torcherino's will act like previous versions.")
    public String online_mode = "";


    public static void initialize()
    {
        ConfigManager.getMarshaller().registerSerializer(Identifier.class, JsonPrimitive::new);
        ConfigManager.getMarshaller().register(Identifier.class, (it) -> (it instanceof String) ? new Identifier((String) it) : new Identifier(it.toString()));
        ConfigManager.getMarshaller().registerTypeAdapter(Tier.class, (it) ->
        {
            String name = it.get(String.class, "name");
            Integer max_speed = it.get(Integer.class, "max_speed");
            Integer xz_range = it.get(Integer.class, "xz_range");
            Integer y_range = it.get(Integer.class, "y_range");
            return new Tier(name, max_speed < 1 ? 1 : max_speed, xz_range < 0 ? 0 : xz_range, y_range < 0 ? 0 : y_range);
        });
        Path sci4meDirectory = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("sci4me");
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
        online_mode = online_mode.toUpperCase();
        if (!(online_mode.equals("ONLINE") || online_mode.equals("RESTART"))) online_mode = "";
        for (Tier tier : tiers) TorcherinoAPI.INSTANCE.registerTier(new Identifier("torcherino", tier.name), tier.max_speed, tier.xz_range, tier.y_range);
        for (Identifier id : blacklisted_blocks) TorcherinoAPI.INSTANCE.blacklistBlock(id);
        for (Identifier id : blacklisted_blockentities) TorcherinoAPI.INSTANCE.blacklistBlockEntity(id);
    }

    private static class Tier
    {
        final String name;
        final int max_speed;
        final int xz_range;
        final int y_range;

        Tier(String name, int max_speed, int xz_range, int y_range)
        {
            this.name = name;
            this.max_speed = max_speed;
            this.xz_range = xz_range;
            this.y_range = y_range;
        }
    }
}
