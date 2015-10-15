package eu.ha3.matmos.serialize.jsonadapters;

import com.google.common.base.Optional;
import com.google.gson.*;
import eu.ha3.matmos.engine.condition.Checkable;
import eu.ha3.matmos.engine.condition.ConditionParser;
import eu.ha3.matmos.engine.condition.ConditionSet;

import java.lang.reflect.Type;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionAdapter implements JsonSerializer<ConditionSet>, JsonDeserializer<ConditionSet>
{
    @Override
    public JsonElement serialize(ConditionSet conditionSet, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonElement conditions = jsonSerializationContext.serialize(conditionSet.serialize());
        JsonObject object = new JsonObject();
        object.addProperty("name", conditionSet.getName());
        object.add("conditions", conditions);
        return object;
    }

    @Override
    public ConditionSet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        String name = jsonElement.getAsJsonObject().get("name").getAsString();
        ConditionSet conditionSet = new ConditionSet(name);
        for (JsonElement jsonElement1 : jsonElement.getAsJsonObject().getAsJsonArray("conditions"))
        {
            Optional<Checkable> condition = ConditionParser.parse(jsonElement1.getAsString()).build();
            if (condition.isPresent())
            {
                conditionSet.addCondition(condition.get());
            }
        }
        return conditionSet;
    }
}
