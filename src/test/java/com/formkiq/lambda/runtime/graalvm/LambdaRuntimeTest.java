/**
 * Copyright [2020] FormKiQ Inc. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.formkiq.lambda.runtime.graalvm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.mock.action.ExpectationResponseCallback;

/** Unit tests for {@link LambdaRuntime}. */
public class LambdaRuntimeTest {

  /** Lambda Request Id. */
  private static final String REQUEST_ID = "testrequestid";

  /** Server Port. */
  private static final int SERVER_PORT = 8001;

  /** Server Host. */
  private static final String SERVER_HOST = "localhost";

  /** {@link ClientAndServer}. */
  private static ClientAndServer mockServer;

  /** {@link InvocationResponseHandler}. */
  private static InvocationResponseHandler invocationResponseHandler =
      new InvocationResponseHandler();

  /** {@link InvocationNextHandler}. */
  private static InvocationNextHandler invocationNextHandler = new InvocationNextHandler();

  /**
   * before class.
   *
   * @throws Exception Exception
   */
  @BeforeClass
  public static void beforeClass() throws Exception {

    mockServer = startClientAndServer(Integer.valueOf(SERVER_PORT));

    add("GET", "/2018-06-01/runtime/invocation/next", invocationNextHandler);
    add(
        "POST",
        "/2018-06-01/runtime/invocation/" + REQUEST_ID + "/response",
        invocationResponseHandler);
    add("POST", "/2018-06-01/runtime/init/error", invocationResponseHandler);
  }

  /** After Class. */
  @AfterClass
  public static void stopServer() {
    mockServer.stop();
  }

  /**
   * Add Url to Mock server.
   *
   * @param method {@link String}
   * @param path {@link String}
   * @param response {@link ExpectationResponseCallback}
   * @throws IOException IOException
   */
  private static void add(
      final String method, final String path, final ExpectationResponseCallback response)
      throws IOException {
    mockServer.when(request().withMethod(method).withPath(path)).respond(response);
  }

  /**
   * Create Lambda Environment.
   *
   * @param handler {@link String}
   * @return {@link Map}
   */
  private Map<String, String> createEnv(final String handler) {
    Map<String, String> env = new HashMap<>();
    env.put("AWS_LAMBDA_RUNTIME_API", SERVER_HOST + ":" + SERVER_PORT);
    env.put("_HANDLER", handler);
    env.put("SINGLE_LOOP", "true");
    return env;
  }

  /** before. */
  @Before
  public void before() {
    invocationNextHandler.setResponseContent("test");
  }

  /**
   * Test Main invoke lambda.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke01() throws Exception {
    // given
    Map<String, String> env = createEnv(TestRequestStreamHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test Invoke with Invalid _HANDLER.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke02() throws Exception {
    // given
    Map<String, String> env = createEnv("com.formkiq.NonExistance.class");

    // when
    LambdaRuntime.invoke(env);

    // then
    String expected =
        "{\"errorMessage\":\"Could not find handler method\",\"errorType\":\"InitError\"}";
    assertEquals(expected, invocationResponseHandler.getResponse());
  }

  /**
   * Test Lambda throwing exception.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke03() throws Exception {
    // given
    Map<String, String> env = createEnv(TestRequestThrowsExceptionHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    String expected =
        "{\"errorMessage\":\"Could not find handler method\",\"errorType\":\"InitError\"}";
    assertEquals(expected, invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputMapVoidHandler}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke04() throws Exception {
    // given
    invocationNextHandler.setResponseContent("{\"data\":\"test\"}");
    Map<String, String> env = createEnv(TestRequestInputMapVoidHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    String expected = "SUCCESS";
    assertEquals(expected, invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringStringHandler}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke05() throws Exception {
    // given
    Map<String, String> env = createEnv(TestRequestInputStringStringHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringIntHandler}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke06() throws Exception {
    // given
    Map<String, String> env = createEnv(TestRequestInputStringIntHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringMapHandler}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke07() throws Exception {
    // given
    Map<String, String> env = createEnv(TestRequestInputStringMapHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringStringHandler}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke08() throws Exception {
    // given
    String clazz =
        "com.formkiq.lambda.runtime.graalvm.TestRequestInputStringStringHandler::handleRequest";
    Map<String, String> env = createEnv(clazz);

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringStringHandler} special method.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke09() throws Exception {
    // given
    String clazz = "com.formkiq.lambda.runtime.graalvm.TestRequestInputStringStringHandler::run";
    Map<String, String> env = createEnv(clazz);

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestInputStringStringHandler} special method and without
   * environment variables.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke10() throws Exception {
    // given
    String clazz = "com.formkiq.lambda.runtime.graalvm.TestRequestInputStringStringHandler::run";
    Map<String, String> env = createEnv(clazz);
    env.remove("SINGLE_LOOP");
    env.remove("AWS_LAMBDA_RUNTIME_API");

    for (Map.Entry<String, String> e : env.entrySet()) {
      System.setProperty(e.getKey(), e.getValue());
    }

    // when
    LambdaRuntime.main(new String[] {});

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
  }

  /**
   * Test invoke Lambda with {@link TestRequestApiGatewayProxyHandler} special method and without
   * environment variables.
   *
   * @throws Exception Exception
   */
  @Test
  public void testInvoke11() throws Exception {
    // given
    invocationNextHandler.setResponseContent("{\"body\":\"this is some data\"}");
    Map<String, String> env = createEnv(TestRequestApiGatewayProxyHandler.class.getName());

    // when
    LambdaRuntime.invoke(env);

    // then
    assertEquals("SUCCESS", invocationResponseHandler.getResponse());
    assertNotNull(System.getProperty("com.amazonaws.xray.traceHeader"));
  }

  /**
   * Test invoke Lambda with {@link APIGatewayProxyRequestEvent}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testApiGatewayProxyRequestEvent01() throws Exception {
    // given
    String payload = loadContent("/APIGatewayProxyRequestEvent/event01.json");

    Gson gson = LambdaRuntime.buildJsonProvider();

    // when
    APIGatewayProxyRequestEvent event =
        (APIGatewayProxyRequestEvent)
            LambdaRuntime.convertToObject(gson, payload, APIGatewayProxyRequestEvent.class);

    // then
    assertEquals("GET", event.getHttpMethod());
    assertEquals("/documents", event.getPath());
    assertEquals("/documents", event.getResource());

    final int expected = 18;
    assertEquals(expected, event.getMultiValueHeaders().size());
    assertFalse(event.getIsBase64Encoded().booleanValue());
  }

  /**
   * Test invoke Lambda with {@link DynamodbEvent}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testDynamodbEvent01() throws Exception {
    // given
    String payload = loadContent("/DynamodbEvent/event01.json");

    Gson gson = LambdaRuntime.buildJsonProvider();

    // when
    DynamodbEvent event =
        (DynamodbEvent) LambdaRuntime.convertToObject(gson, payload, DynamodbEvent.class);

    // then
    assertEquals(2, event.getRecords().size());

    DynamodbStreamRecord record = event.getRecords().get(0);
    assertEquals("us-east-2", record.getAwsRegion());
    assertEquals("a2e11a1268fa1f98b2dd17fd95cffc0f", record.getEventID());
    assertEquals("INSERT", record.getEventName());
    assertEquals("aws:dynamodb", record.getEventSource());
    assertEquals(
        "arn:aws:dynamodb:us-east-2:1111111111:table/test/stream/2022-11-13T00:56:05.381",
        record.getEventSourceARN());
    assertEquals("1.1", record.getEventVersion());
    assertNull(record.getUserIdentity());

    StreamRecord db = record.getDynamodb();
    assertNull(db.getApproximateCreationDateTime());
    assertEquals(
        "{SK={S: document,}, PK={S: docs#acd4be1b-9466-4dcd-b8b8-e5b19135b460,}}",
        db.getKeys().toString());

    String newImageExpected =
        "{path={S: /somewhere/else/test.pdf,}, "
            + "lastModifiedDate={S: 2022-11-13T01:07:01+0000,}, "
            + "inserteddate={S: 2022-11-13T01:07:01+0000,}, SK={S: document,}, "
            + "documentId={S: acd4be1b-9466-4dcd-b8b8-e5b19135b460,}, "
            + "PK={S: docs#acd4be1b-9466-4dcd-b8b8-e5b19135b460,}, "
            + "userId={S: testadminuser@formkiq.com,}, "
            + "md#content={S: this is some content karate,}}";
    assertEquals(newImageExpected, db.getNewImage().toString());
    assertEquals("4409700000000012453238087", db.getSequenceNumber());
    assertEquals("299", db.getSizeBytes().toString());
    assertEquals("NEW_AND_OLD_IMAGES", db.getStreamViewType());
  }

  /**
   * Test invoke Lambda with {@link S3Event}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testS3Event01() throws Exception {
    // given
    String payload = loadContent("/S3Event/event01.json");

    Gson gson = LambdaRuntime.buildJsonProvider();

    // when
    S3Event event = (S3Event) LambdaRuntime.convertToObject(gson, payload, S3Event.class);

    // then
    assertEquals(1, event.getRecords().size());

    S3EventNotificationRecord record = event.getRecords().get(0);
    assertEquals("us-east-1", record.getAwsRegion());
    assertEquals("ObjectCreated:Put", record.getEventName());
    assertEquals("aws:s3", record.getEventSource());
    assertNull(record.getEventTime());
    assertEquals("2.0", record.getEventVersion());
    assertEquals("EXAMPLE", record.getUserIdentity().getPrincipalId());

    assertEquals("127.0.0.1", record.getRequestParameters().getSourceIPAddress());
    assertEquals(
        "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH",
        record.getResponseElements().getxAmzId2());
    assertEquals("EXAMPLE123456789", record.getResponseElements().getxAmzRequestId());

    assertEquals("arn:aws:s3:::example-bucket", record.getS3().getBucket().getArn());
    assertEquals("example-bucket", record.getS3().getBucket().getName());

    assertEquals("0123456789abcdef0123456789abcdef", record.getS3().getObject().geteTag());
    assertEquals("b53c92cf-f7b9-4787-9541-76574ec70d71", record.getS3().getObject().getKey());
    assertEquals("0A1B2C3D4E5F678901", record.getS3().getObject().getSequencer());
    assertNull(record.getS3().getObject().getVersionId());
    assertEquals("0A1B2C3D4E5F678901", record.getS3().getObject().getSequencer());

    assertEquals("1.0", record.getS3().getS3SchemaVersion());
  }

  /**
   * Test invoke Lambda with {@link SqsEvent}.
   *
   * @throws Exception Exception
   */
  @Test
  public void testSqsEvent01() throws Exception {
    // given
    String payload = loadContent("/SqsEvent/event01.json");

    Gson gson = LambdaRuntime.buildJsonProvider();

    // when
    SQSEvent event = (SQSEvent) LambdaRuntime.convertToObject(gson, payload, SQSEvent.class);

    // then
    assertEquals(1, event.getRecords().size());

    SQSMessage record = event.getRecords().get(0);
    assertEquals(
        "{ApproximateReceiveCount=1, SentTimestamp=1655525998108, "
            + "SenderId=AIDAJQR6QDGQ7PATMSYEY, "
            + "ApproximateFirstReceiveTimestamp=1655525998113}",
        record.getAttributes().toString());
    assertEquals("us-east-2", record.getAwsRegion());
    assertNotNull(record.getBody());
    assertEquals("aws:sqs", record.getEventSource());
    assertEquals(
        "arn:aws:sqs:us-east-2:111111111111:test-QtDaIStrYbFb", record.getEventSourceArn());
    assertEquals("18825a1dbc4503da7bb93b2b6585d704", record.getMd5OfBody());
    assertNull(record.getMd5OfMessageAttributes());
    assertEquals("{}", record.getMessageAttributes().toString());
    assertEquals("a781b6d9-6eff-4526-92a2-bb1b587e2cde", record.getMessageId());
    assertNotNull(record.getReceiptHandle());
  }

  private String loadContent(final String filename) throws IOException {
    try (InputStream is = getClass().getResourceAsStream(filename)) {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
  }
}
