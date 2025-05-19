package com.formkiq.lambda.runtime.graalvm;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/** {@link JsonSerializer} for {@link DateTime}. */
public class DateTimeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
  /** {@link DateTimeFormatter}. */
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  @Override
  public JsonElement serialize(
      final DateTime src, final Type typeOfSrc, final JsonSerializationContext context) {
    return new JsonPrimitive(FORMATTER.print(src));
  }

  @Override
  public DateTime deserialize(
      final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
      throws JsonParseException {
    return FORMATTER.parseDateTime(json.getAsString());
  }
}
