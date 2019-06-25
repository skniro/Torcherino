package torcherino.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigManager
{

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String DEFAULT_EXTENSION = ".json5";

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz      The class of the POJO that will store all our properties
	 * @param configName The name of the config file
	 * @return A new config Object containing all our options from the config file
	 */
	public static <T> T loadConfig(Class<T> clazz, String configName)
	{
		try
		{
			File file = FMLPaths.CONFIGDIR.get().resolve(configName).toFile();
			Jankson jankson = Jankson.builder().build();
			//Generate config file if it doesn't exist
			if (!file.exists())
			{
				saveConfig(clazz.newInstance(), configName);
			}
			try
			{
				JsonObject json = jankson.load(file);
				String cleaned = json.toJson(false, true); //remove comments
				T result = jankson.fromJson(json, clazz);
				//check if the config file is outdated. If so overwrite it
				JsonElement jsonElementNew = jankson.toJson(clazz.newInstance());
				if (jsonElementNew instanceof JsonObject)
				{
					JsonObject jsonNew = (JsonObject) jsonElementNew;
					if (json.getDelta(jsonNew).size() >= 0)
					{
						saveConfig(result, configName);
					}
				}
				return result;
			}
			catch (IOException e)
			{
				LOGGER.warn("Failed to load config File {}: {}", configName, e);
			}
		}
		catch (SyntaxError syntaxError)
		{
			LOGGER.warn("Failed to load config File {}: {}", configName, syntaxError);
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			LOGGER.warn("Failed to create new config file for {}: {}", configName, e);
		}
		//Something obviously went wrong, create placeholder config
		LOGGER.warn("Creating placeholder config for {}...", configName);
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			LOGGER.warn("Failed to create placeholder config for {}: {}", configName, e);
		}
		//this is ... unfortunate
		return null;
	}

	/**
	 * Saves a POJO Config object to the disk. This is mostly used to create new configs if they don't already exist
	 *
	 * @param object     The Config we want to save
	 * @param configName The filename of our config.
	 */
	public static void saveConfig(Object object, String configName)
	{
		Jankson jankson = Jankson.builder().build();
		JsonElement json = jankson.toJson(object);
		String result = json.toJson(true, true);
		try
		{
			File file = FMLPaths.CONFIGDIR.get().resolve(configName).toFile();
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to write to config file {}: {}", configName, e);
		}
	}
}
