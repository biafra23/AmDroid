package com.jaeckel.amdroid.api.model;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * User: biafra
 * Date: 9/29/11
 * Time: 12:36 AM
 */
public class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date>, InstanceCreator<Date> {

  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive("2011-10-26T07:30:34Z");
  }

  public Date createInstance(Type type) {
    return new Date();
  }

  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    String jsonString = json.getAsJsonPrimitive().getAsString();

    DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
    DateTime dt = fmt.parseDateTime(jsonString);
    return dt.toDate();

  }
}
