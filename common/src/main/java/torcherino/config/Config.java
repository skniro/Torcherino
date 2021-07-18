package torcherino.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;
import torcherino.api.TorcherinoAPI;
import torcherino.platform.PlatformUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Config {
    public static Config INSTANCE;

    //@Comment("\nDefines how much faster randoms ticks are applied compared to what they should be.\nValid Range: 1 to 4096")
    public final int random_tick_rate = 4;

    //@Comment("Log torcherino placement (Intended for server use)")
    public final boolean log_placement = PlatformUtils.getInstance().isDedicatedServer();

    //@Comment("\nAdd a block by identifier to the blacklist.\nExamples: \"minecraft:dirt\", \"minecraft:furnace\"")
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final ResourceLocation[] blacklisted_blocks = new ResourceLocation[]{};

    //@Comment("\nAdd a block entity by identifier to the blacklist.\nExamples: \"minecraft:furnace\", \"minecraft:mob_spawner\"")
    @SuppressWarnings({"MismatchedReadAndWriteOfArray", "SpellCheckingInspection"})
    private final ResourceLocation[] blacklisted_blockentities = new ResourceLocation[]{};

    //@Comment("\nAllows new custom torcherino tiers to be added.\nThis also allows for each tier to have their own max max_speed and ranges.")
    private final Tier[] tiers = new Tier[]{new Tier("normal", 4, 4, 1), new Tier("compressed", 36, 4, 1), new Tier("double_compressed", 324, 4, 1)};

    //@Comment("\nWhen set to ONLINE, Torcherino's only run if the player is currently online\nIf set to RESTART then Torcherino's will run for anyone who has logged in since the server started.\nAnything else then Torcherino's will act like previous versions.")
    public String online_mode = "";


    public static void initialize() {
        Gson gson = new GsonBuilder().disableInnerClassSerialization()
                                     .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                                     .setPrettyPrinting()
                                     .create();
        var configDir = PlatformUtils.getInstance().getConfigPath();
        var logger = LogManager.getLogger("torcherino-config");
        var marker = new MarkerManager.Log4jMarker("torcherino");
        Config config = null;
        try {
            Files.createDirectories(configDir.getParent());
        } catch (IOException e) {
            logger.warn(marker, "Failed to create directory required for torcherino config, using default config.");
            config = new Config();
        }
        if (config == null) {
            if (Files.exists(configDir)) {
                try (var reader = Files.newBufferedReader(configDir)) {
                    config = gson.fromJson(reader, Config.class);
                } catch (IOException e) {
                    logger.warn(marker, "Failed to read torcherino config file, using default config.");
                    config = new Config();
                }
            } else {
                config = new Config();
                try (var writer = Files.newBufferedWriter(configDir, StandardOpenOption.CREATE_NEW)) {
                    gson.toJson(config, writer);
                } catch (IOException e) {
                    logger.warn(marker, "Failed to save default torcherino config file.");
                }
            }

        }
        INSTANCE = config;
        INSTANCE.onConfigLoaded();
    }

    private void onConfigLoaded() {
        online_mode = online_mode.toUpperCase();
        if (!(online_mode.equals("ONLINE") || online_mode.equals("RESTART"))) {
            online_mode = "";
        }
        for (Tier tier : tiers) {
            TorcherinoAPI.INSTANCE.registerTier(new ResourceLocation("torcherino", tier.name), tier.max_speed, tier.xz_range, tier.y_range);
        }
        for (ResourceLocation id : blacklisted_blocks) {
            TorcherinoAPI.INSTANCE.blacklistBlock(id);
        }
        for (ResourceLocation id : blacklisted_blockentities) {
            TorcherinoAPI.INSTANCE.blacklistBlockEntity(id);
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    static class Tier {
        final String name;
        final int max_speed;
        final int xz_range;
        final int y_range;

        Tier(String name, int max_speed, int xz_range, int y_range) {
            this.name = name;
            this.max_speed = max_speed;
            this.xz_range = xz_range;
            this.y_range = y_range;
        }
    }
}
