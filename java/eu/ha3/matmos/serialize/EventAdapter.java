package eu.ha3.matmos.serialize;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author dags_ <dags@dags.me>
 */

public class EventAdapter<T> implements JsonDeserializer<T>
{
    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        if (jsonElement.getAsJsonObject().has("repeatable"))
        {
            return jsonDeserializationContext.deserialize(jsonElement, StreamEventSerialize.class);
        }
        return jsonDeserializationContext.deserialize(jsonElement, EventSerialize.class);
    }
}
