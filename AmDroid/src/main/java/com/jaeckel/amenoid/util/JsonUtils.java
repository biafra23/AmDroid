package com.jaeckel.amenoid.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * User: biafra
 * Date: 11/12/11
 * Time: 9:48 PM
 */
public class JsonUtils {

  static void prettyprint(JsonReader reader, JsonWriter writer) throws IOException {
          while (true) {
              JsonToken token = reader.peek();
              switch (token) {
              case BEGIN_ARRAY:
                  reader.beginArray();
                  writer.beginArray();
                  break;
              case END_ARRAY:
                  reader.endArray();
                  writer.endArray();
                  break;
              case BEGIN_OBJECT:
                  reader.beginObject();
                  writer.beginObject();
                  break;
              case END_OBJECT:
                  reader.endObject();
                  writer.endObject();
                  break;
              case NAME:
                  String name = reader.nextName();
                  writer.name(name);
                  break;
              case STRING:
                  String s = reader.nextString();
                  writer.value(s);
                  break;
              case NUMBER:
                  String n = reader.nextString();
                  writer.value(new BigDecimal(n));
                  break;
              case BOOLEAN:
                  boolean b = reader.nextBoolean();
                  writer.value(b);
                  break;
              case NULL:
                  reader.nextNull();
                  writer.nullValue();
                  break;
              case END_DOCUMENT:
                  return;
              }
          }
      }
}
