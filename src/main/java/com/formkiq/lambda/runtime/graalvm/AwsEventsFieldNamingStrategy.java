package com.formkiq.lambda.runtime.graalvm;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/** AWS Events Field {@link FieldNamingStrategy}. */
public class AwsEventsFieldNamingStrategy implements FieldNamingStrategy {

  @Override
  public String translateName(final Field f) {
    return f.getName();
  }

  @Override
  public List<String> alternateNames(final Field field) {
    String fieldName = translateName(field);

    return Stream.of(
            FieldNamingPolicy.UPPER_CAMEL_CASE.translateName(field),
            separateCamelCaseIncludeDigit(fieldName, '-').toLowerCase(Locale.ENGLISH),
            convertUpperCaseSuffix(fieldName, "Arn"),
            fieldName.toLowerCase(),
            fieldName.toUpperCase())
        .filter(s -> !s.isBlank() && !s.equals(fieldName))
        .distinct()
        .toList();
  }

  static String convertUpperCaseSuffix(final String input, final String ext) {
    String s = "";
    if (input.endsWith(ext)) {
      s = input.substring(0, input.length() - ext.length()) + ext.toUpperCase();
    }
    return s;
  }

  /**
   * Converts the field name that uses camel-case define word separation into separate words that
   * are separated by the provided {@code separator}.
   *
   * @param name {@link String}
   * @param separator char
   * @return String
   */
  static String separateCamelCaseIncludeDigit(final String name, final char separator) {
    StringBuilder translation = new StringBuilder();
    for (int i = 0, length = name.length(); i < length; i++) {
      char character = name.charAt(i);
      if ((Character.isUpperCase(character) || Character.isDigit(character))
          && !translation.isEmpty()) {
        translation.append(separator);
      }
      translation.append(character);
    }
    return translation.toString();
  }
}
