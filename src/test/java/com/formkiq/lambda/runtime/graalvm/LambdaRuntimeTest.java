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
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    assertEquals("test data result", invocationResponseHandler.getResponse());
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
    String expected = "";
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
    String expected = "this is a test string";
    assertEquals(expected, invocationResponseHandler.getResponse());
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
    String expected = "98";
    assertEquals(expected, invocationResponseHandler.getResponse());
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
    String expected = "{\"test\":\"123\"}";
    assertEquals(expected, invocationResponseHandler.getResponse());
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
    String expected = "this is a test string";
    assertEquals(expected, invocationResponseHandler.getResponse());
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
    String expected = "this is a run string";
    assertEquals(expected, invocationResponseHandler.getResponse());
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

    System.out.println("MAP: " + env);
    // when
    LambdaRuntime.main(new String[] {});

    // then
    String expected = "this is a run string";
    assertEquals(expected, invocationResponseHandler.getResponse());
  }
}
