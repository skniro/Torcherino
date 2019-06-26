package torcherino.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigManager
{

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Jankson jankson = new Jankson.Builder().build();

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz      The class of the POJO that will store all our properties
	 * @param configFile The config file
	 * @return A new config Object containing all our options from the config file
	 */
	@SuppressWarnings("ConstantConditions") public static <T> T loadConfig(Class<T> clazz, File configFile)
	{
		try
		{
			//Generate config file if it doesn't exist
			if (!configFile.exists())
			{
				T instance = clazz.newInstance();
				saveConfig(instance, configFile);
				return instance;
			}
			try
			{
				JsonObject json = jankson.load(configFile);
				String cleaned = json.toJson(false, true); //remove comments
				T result = jankson.fromJson(json, clazz);
				//check if the config file is outdated. If so overwrite it
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
		//Something obviously went wrong, create placeholder config
		LOGGER.warn("Creating placeholder config for {}...", configFile.getName());
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			LOGGER.warn("Failed to create placeholder config for {}: {}", configFile.getName(), e);
		}
		//this is ... unfortunate
		return null;
	}

	/**
	 * Saves a POJO Config object to the disk. This is mostly used to create new configs if they don't already exist
	 *
	 * @param object     The Config we want to save
	 * @param configFile The config file.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored") public static void saveConfig(Object object, File configFile)
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
