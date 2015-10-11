package eu.ha3.matmos.engine.serialize.jsonadapters;

import com.google.gson.*;
import eu.ha3.matmos.engine.serialize.EventSerialize;
import eu.ha3.matmos.engine.serialize.StreamEventSerialize;

import java.lang.reflect.Type;

/**
 * @author dags_ <dags@dags.me>
 */

public class EventAdapter implements JsonDeserializer<EventSerialize>, JsonSerializer<EventSerialize>
{
    @Override
    public EventSerialize deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        if (jsonElement.getAsJsonObject().has("repeatable"))
        {
            return jsonDeserializationContext.deserialize(jsonElement, StreamEventSerialize.class);
        }
        return jsonDeserializationContext.deserialize(jsonElement, EventSerialize.class);
    }

    @Override
    public JsonElement serialize(EventSerialize eventSerialize, Type type, JsonSerializationContext jsonSerializationContext)
    {
        if (eventSerialize instanceof StreamEventSerialize)
        {
            return jsonSerializationContext.serialize((StreamEventSerialize) eventSerialize);
        }
        return jsonSerializationContext.serialize(eventSerialize);
    }
}
