package torcherino.config;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class ResourceLocationAdapter implements JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {

    @Override
    public JsonElement serialize(ResourceLocation resourceLocation, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(resourceLocation.toString());
    }

    @Override
    public ResourceLocation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ResourceLocation.parse(GsonHelper.convertToString(jsonElement, "location"));
    }
}
