package com.yoavst.quickapps.launcher

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer

import java.lang.reflect.Type

/**
 * Created by Yoav.
 */
public class LauncherDeSerializer : JsonDeserializer<ListItem>, JsonSerializer<ListItem> {
    throws(javaClass<JsonParseException>())
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ListItem {
        val jsonObject = json.getAsJsonObject()
        val name = jsonObject.get("name").getAsString()
        val activity = if (jsonObject.has("activity-name")) jsonObject.get("activity-name").getAsString() else ""
        val enabled = !jsonObject.has("enabled") || jsonObject.get("enabled").getAsBoolean()
        return ListItem(name, null, activity, enabled)
    }

    override fun serialize(src: ListItem, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", src.name)
        jsonObject.addProperty("enabled", src.enabled)
        jsonObject.addProperty("activity-name", src.activity)
        return jsonObject
    }
}