package torcherino.config;

import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Philosophy of this config: Anything on the right of the equals sign is up to the serializer.
 * This means comments inside objects must be handled by the serializer.
 * To put it simply, this is a really basic config format.
 */
public class Manager
{

	private static Map<Class, TypeSerializer> serializers = new HashMap<>();
	// Special serializer for arrays
	private static final ListSerializer listSerializer = new ListSerializer();
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
		if (data instanceof List)
		{
			return listSerializer.serialize((List) data);
		}
		TypeSerializer serializer = getSerializer(dataClass);
		if (serializer != null) return serializer.serialize(data);
		return null;
	}

	public static <T> List<T> deserializeList(String data, Class<? extends List> type, Class<T> dataType)
	{
		if (List.class.isAssignableFrom(type))
		{
			return listSerializer.deserialize(data, dataType);
		}
		return null;
	}

	public static Object deserialize(String data, Class type)
	{
		TypeSerializer serializer = getSerializer(type);
		if (serializer != null) return serializer.deserialize(data);
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

	private static class ListSerializer
	{

		public String serialize(List<Object> data)
		{
			StringBuilder builder = new StringBuilder("[");
			for (int i = 0; i < data.size(); i++)
			{
				if (i != 0) builder.append(", ");
				builder.append(Manager.serialize(data.get(i)));
			}
			builder.append("]");
			return builder.toString();
		}

		public <T> List<T> deserialize(String data, Class<T> type)
		{
			if (data.equals("[]")) return new ArrayList<T>();
			if (data.length() < 3) return null;
			ArrayList<T> returnValue = new ArrayList<T>();
			int startPos = 1;
			int endPos = 1;
			int level = 0;
			StringBuilder d = new StringBuilder();
			while (endPos < data.length() - 1)
			{
				char c = data.charAt(endPos);
				d.append(c);
				if (c == '(') level += 1;
				else if (c == '[') level += 2;
				else if (c == '{') level += 4;
				else if (c == ')') level -= 1;
				else if (c == ']') level -= 2;
				else if (c == '}') level -= 4;
				else if (c == ',' && level == 0)
				{
					// We probably have a value here
					returnValue.add((T) Manager.deserialize(data.substring(startPos, endPos), type));
					endPos = startPos = endPos + 1;
					if (data.charAt(endPos) == ' ') endPos = startPos++;
				}
				endPos++;
			}
			// Our last value should be here.
			if (level == 0)
			{
				returnValue.add((T) Manager.deserialize(data.substring(startPos, endPos), type));
			}
			else
			{
				// data is malformed (braces dont match)
				return null;
			}
			return returnValue;
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
