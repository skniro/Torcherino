package torcherino.config;

import com.google.gson.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class IdentifierAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

    @Override
    public JsonElement serialize(Identifier Identifier, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(Identifier.toString());
    }

    @Override
    public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Identifier.parse(GsonHelper.convertToString(jsonElement, "location"));
    }
}
