package torcherino.config;

import net.minecraft.util.ResourceLocation;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Manager
{

	private static Map<Class, TypeSerializer> serializers = new HashMap<>();
	// Special serializer for arrays
	private static final TypeSerializer<Object> arraySerializer = new ArraySerializer();
	static
	{
		serializers.put(Integer.class, new IntegerSerializer());
		serializers.put(String.class, new StringSerializer());
		serializers.put(Boolean.class, new BooleanSerializer());
		serializers.put(ResourceLocation.class, new ResourceLocationSerializer());
	}
	public static TypeSerializer getSerializer(Class type)
	{
		return serializers.getOrDefault(type, null);
	}

	public static <T> String serialize(T data)
	{
		Class dataClass = data.getClass();
		if (dataClass.isArray())
		{
			return arraySerializer.serialize(data);
		}
		TypeSerializer serializer = getSerializer(dataClass);
		if (serializer != null) return serializer.serialize(data);
		return null;
	}

	public static <T> T deserialize(String data, Class type)
	{
		if (type.isArray())
		{
			return (T) arraySerializer.deserialize(data);
		}
		TypeSerializer serializer = getSerializer(type);
		if (serializer != null) return (T) serializer.deserialize(data);
		return null;
	}


	// Built-in Serializers
	private static class IntegerSerializer implements TypeSerializer<Integer>
	{

		@Override public String serialize(Integer data){ return data.toString();}


		@Override public Integer deserialize(String data){ return Integer.valueOf(data); }
	}

	private static class StringSerializer implements TypeSerializer<String>
	{

		@Override public String serialize(String data)
		{
			return "\"" + data + "\"";
		}

		@Override public String deserialize(String data)
		{
			return data.substring(1, data.length() - 1);
		}
	}

	private static class BooleanSerializer implements TypeSerializer<Boolean>
	{

		@Override public String serialize(Boolean data)
		{
			return data.toString();
		}

		@Override public Boolean deserialize(String data)
		{
			return Boolean.valueOf(data);
		}
	}

	private static class ArraySerializer implements TypeSerializer<Object>
	{

		@Override public String serialize(Object data)
		{
			StringBuilder builder = new StringBuilder("[");
			for (int i = 0; i < Array.getLength(data); i++)
			{
				if (i != 0) builder.append(", ");
				builder.append(Manager.serialize(Array.get(data, i)));
			}
			builder.append("]");
			return builder.toString();
		}

		@Override public Object deserialize(String data)
		{
			// todo deserialize
			return null;
		}
	}

	private static class ResourceLocationSerializer implements TypeSerializer<ResourceLocation>
	{
		StringSerializer stringSerializer = (StringSerializer) getSerializer(String.class);

		@Override public String serialize(ResourceLocation data)
		{
			return stringSerializer.serialize(data.toString());
		}

		@Override public ResourceLocation deserialize(String data)
		{
			return new ResourceLocation(stringSerializer.deserialize(data));
		}
	}
}
