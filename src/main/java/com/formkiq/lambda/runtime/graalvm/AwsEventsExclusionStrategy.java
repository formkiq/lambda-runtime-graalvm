package com.formkiq.lambda.runtime.graalvm;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/** {@link ExclusionStrategy} for Aws Events. */
public class AwsEventsExclusionStrategy implements ExclusionStrategy {

  /** Skip Classes. */
  private static final Collection<Class<?>> CLASSES = List.of(ByteBuffer.class);

  /** Skip Fields. */
  private static final Collection<String> FIELDS = List.of("approximateCreationDateTime");

  @Override
  public boolean shouldSkipField(final FieldAttributes fieldAttributes) {
    return FIELDS.contains(fieldAttributes.getName());
  }

  @Override
  public boolean shouldSkipClass(final Class<?> clazz) {
    return CLASSES.contains(clazz);
  }
}
