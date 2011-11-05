package com.jaeckel.amenoid.api.model;

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

  DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
  DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    DateTime dateTime = new DateTime(src);
    return new JsonPrimitive(formatter.print(dateTime));
  }

  public Date createInstance(Type type) {
    return new Date();
  }

  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {

    String jsonString = json.getAsJsonPrimitive().getAsString();
    DateTime dt = parser.parseDateTime(jsonString);
    return dt.toDate();

  }
}
