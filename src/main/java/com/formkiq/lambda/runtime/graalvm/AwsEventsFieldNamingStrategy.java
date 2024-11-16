package com.formkiq.lambda.runtime.graalvm;

import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/** AWS Events Field {@link FieldNamingStrategy}. */
public class AwsEventsFieldNamingStrategy implements FieldNamingStrategy {

  /** Field Mapping. */
  private static final Map<String, String> FIELD_MAPPING =
      Map.of("xAmzId2", "x-amz-id-2", "xAmzRequestId", "x-amz-request-id");

  @Override
  public List<String> translateName(final Field field) {
    String fieldName = field.getName();
    String text = fieldName.endsWith("Arn") ? fieldName.replaceAll("Arn", "ARN") : "";

    return FIELD_MAPPING.containsKey(fieldName)
        ? Collections.singletonList(FIELD_MAPPING.get(fieldName))
        : new LinkedHashSet<>(
                List.of(
                    fieldName,
                    text,
                    upperCaseFirstLetter(fieldName),
                    fieldName.toLowerCase(),
                    fieldName.toUpperCase()))
            .stream().filter(s -> !s.isBlank()).toList();
  }

  private static String upperCaseFirstLetter(final String s) {
    return s != null && !s.isEmpty() ? Character.toUpperCase(s.charAt(0)) + s.substring(1) : s;
  }
}
