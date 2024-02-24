package com.formkiq.lambda.runtime.graalvm;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;
import java.util.Map;

/** AWS Events Field {@link FieldNamingStrategy}. */
public class AwsEventsFieldNamingStrategy implements FieldNamingStrategy {

  /** {@link DynamodbEvent} Field Mapping. */
  private static final Map<Class<?>, Map<String, String>> FIELD_MAPPING =
      Map.of(
          DynamodbEvent.class,
          Map.of("records", "Records"),
          StreamRecord.class,
          Map.of(
              "newImage",
              "NewImage",
              "oldImage",
              "OldImage",
              "keys",
              "Keys",
              "sequenceNumber",
              "SequenceNumber",
              "sizeBytes",
              "SizeBytes",
              "streamViewType",
              "StreamViewType"),
          AttributeValue.class,
          Map.of(
              "s",
              "S",
              "n",
              "N",
              "sS",
              "SS",
              "nS",
              "NS",
              "m",
              "M",
              "l",
              "L",
              "nULLValue",
              "NULLValue",
              "bOOL",
              "BOOL"),
          S3EventNotification.class,
          Map.of("records", "Records"),
          S3EventNotification.S3EventNotificationRecord.class,
          Map.of("eventTime", "EventTime"),
          S3EventNotification.ResponseElementsEntity.class,
          Map.of("xAmzId2", "x-amz-id-2", "xAmzRequestId", "x-amz-request-id"),
          SQSEvent.class,
          Map.of("records", "Records"),
          SQSEvent.SQSMessage.class,
          Map.of("eventSourceArn", "eventSourceARN"));

  @Override
  public String translateName(final Field field) {

    String fieldName = field.getName();

    Map<String, String> fields = FIELD_MAPPING.get(field.getDeclaringClass());

    if (fields != null && fields.containsKey(fieldName)) {
      fieldName = fields.get(fieldName);
    }

    return fieldName;
  }
}
