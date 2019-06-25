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
import java.nio.file.Path;

public class ConfigManager
{

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Jankson.Builder builder = new Jankson.Builder();

	/**
	 * Loads a .config file from the config folder and parses it to a POJO.
	 *
	 * @param clazz          The class of the POJO that will store all our properties
	 * @param configFilePath The path to the config file
	 * @return A new config Object containing all our options from the config file
	 */
	@SuppressWarnings("ConstantConditions") public static <T> T loadConfig(Class<T> clazz, Path configFilePath)
	{
		try
		{
			File file = configFilePath.toFile();
			Jankson jankson = builder.build();
			//Generate config file if it doesn't exist
			if (!file.exists())
			{
				T instance = clazz.newInstance();
				saveConfig(instance, configFilePath);
				return instance;
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
						saveConfig(result, configFilePath);
					}
				}
				return result;
			}
			catch (IOException e)
			{
				LOGGER.warn("Failed to load config File {}: {}", configFilePath.getFileName(), e);
			}
		}
		catch (SyntaxError syntaxError)
		{
			LOGGER.warn("Failed to load config File {}: {}", configFilePath.getFileName(), syntaxError);
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			LOGGER.warn("Failed to create new config file for {}: {}", configFilePath.getFileName(), e);
		}
		//Something obviously went wrong, create placeholder config
		LOGGER.warn("Creating placeholder config for {}...", configFilePath.getFileName());
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			LOGGER.warn("Failed to create placeholder config for {}: {}", configFilePath.getFileName(), e);
		}
		//this is ... unfortunate
		return null;
	}

	/**
	 * Saves a POJO Config object to the disk. This is mostly used to create new configs if they don't already exist
	 *
	 * @param object         The Config we want to save
	 * @param configFilePath The path to the config file.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored") public static void saveConfig(Object object, Path configFilePath)
	{
		Jankson jankson = builder.build();
		JsonElement json = jankson.toJson(object);
		String result = json.toJson(true, true);
		try
		{
			File file = configFilePath.toFile();
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		}
		catch (IOException e)
		{
			LOGGER.warn("Failed to write to config file {}: {}", configFilePath.getFileName(), e);
		}
	}
}
