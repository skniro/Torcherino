package torcherino.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.Marshaller;
import blue.endless.jankson.api.SyntaxError;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import torcherino.config.Config.Tier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Modified version of CottonMC's Config Manager which can be found here: https://github.com/CottonMC/Cotton/blob/af5d07f01aebc50fc28203881a8a9dcc1a9987cc/src/main/java/io/github/cottonmc/cotton/config/ConfigManager.java
 */
@SuppressWarnings({ "SameParameterValue", "WeakerAccess" })
public class ConfigManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Jankson jankson = new Jankson.Builder()
            .registerSerializer(Identifier.class, (identifier, marshaller) -> new JsonPrimitive(identifier))
            .registerDeserializer(String.class, Identifier.class, (it, marshaller) -> new Identifier(it))
            .registerDeserializer(JsonObject.class, Tier.class, (it, marshaller) -> {
                String name = it.get(String.class, "name");
                Integer max_speed = it.get(Integer.class, "max_speed");
                Integer xz_range = it.get(Integer.class, "xz_range");
                Integer y_range = it.get(Integer.class, "y_range");
                return new Tier(name, max_speed < 1 ? 1 : max_speed, xz_range < 0 ? 0 : xz_range, y_range < 0 ? 0 : y_range);
            })
            .build();

    @SuppressWarnings("ConstantConditions")
    static <T> T loadConfig(Class<T> clazz, File configFile)
    {
        try
        {
            if (!configFile.exists())
            {
                T instance = clazz.newInstance();
                saveConfig(instance, configFile);
                return instance;
            }
            try
            {
                JsonObject json = jankson.load(configFile);
                T result = jankson.fromJson(json, clazz);
                JsonElement jsonElementNew = jankson.toJson(clazz.newInstance());
                if (jsonElementNew instanceof JsonObject)
                {
                    JsonObject jsonNew = (JsonObject) jsonElementNew;
                    if (json.getDelta(jsonNew).size() >= 0)
                    {
                        saveConfig(result, configFile);
                    }
                }
                return result;
            }
            catch (IOException e)
            {
                LOGGER.warn("Failed to load config File {}: {}", configFile.getName(), e);
            }
        }
        catch (SyntaxError syntaxError)
        {
            LOGGER.warn("Failed to load config File {}: {}", configFile.getName(), syntaxError);
        }
        catch (IllegalAccessException | InstantiationException e)
        {
            LOGGER.warn("Failed to create new config file for {}: {}", configFile.getName(), e);
        }
        LOGGER.warn("Creating placeholder config for {}...", configFile.getName());
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            LOGGER.warn("Failed to create placeholder config for {}: {}", configFile.getName(), e);
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveConfig(Object object, File configFile)
    {
        JsonElement json = jankson.toJson(object);
        String result = json.toJson(true, true);
        try
        {
            if (!configFile.exists()) configFile.createNewFile();
            FileOutputStream out = new FileOutputStream(configFile, false);
            out.write(result.getBytes());
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            LOGGER.warn("Failed to write to config file {}: {}", configFile.getName(), e);
        }
    }
}