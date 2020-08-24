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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;

/** Wrapper for the AWS Lambda Runtime. */
public class LambdaRuntime {

  /** Lambda Version. */
  private static final String LAMBDA_VERSION_DATE = "2018-06-01";
  /** Lambda Runtime URL. */
  private static final String LAMBDA_RUNTIME_URL_TEMPLATE =
      "http://{0}/{1}/runtime/invocation/next";
  /** Lambda Runtime Invocation URL. */
  private static final String LAMBDA_INVOCATION_URL_TEMPLATE =
      "http://{0}/{1}/runtime/invocation/{2}/response";
  /** Lambda Init Error URL. */
  private static final String LAMBDA_INIT_ERROR_URL_TEMPLATE = "http://{0}/{1}/runtime/init/error";
  /** Lambda Error Inocation Url. */
  private static final String LAMBDA_ERROR_URL_TEMPLATE =
      "http://{0}/{1}/runtime/invocation/{2}/error";
  /** Error Response Template. */
  private static final String ERROR_RESPONSE_TEMPLATE =
      "'{'\"errorMessage\":\"{0}\",\"errorType\":\"{1}\"'}'";

  /**
   * Find {@link RequestHandler} "handleRequest".
   *
   * @param clazz {@link Class}
   * @param methodName {@link String}
   * @return {@link Method}
   */
  private static Method findRequestHandlerMethod(final Class<?> clazz, final String methodName) {
    Method method = null;

    for (Method m : clazz.getMethods()) {
      if (m.getName().equalsIgnoreCase(methodName)) {
        method = m;
        break;
      }
    }
    return method;
  }

  /**
   * Handle Init Error.
   *
   * @param env {@link Map}
   * @param ex {@link Exception}
   * @throws IOException IOException
   */
  public static void handleInitError(final Map<String, String> env, final Exception ex)
      throws IOException {

    String runtimeApi = env.get("AWS_LAMBDA_RUNTIME_API");
    String initErrorUrl =
        MessageFormat.format(LAMBDA_INIT_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE);

    String error =
        MessageFormat.format(ERROR_RESPONSE_TEMPLATE, "Could not find handler method", "InitError");
    HttpClient.post(initErrorUrl, error);

    ex.printStackTrace();
  }

  /**
   * Handle Lambda Invocation Errors.
   *
   * @param env {@link Map}
   * @param requestId {@link String}
   * @param ex {@link Exception}
   */
  public static void handleInvocationException(
      final Map<String, String> env, final String requestId, final Exception ex) {
    ex.printStackTrace();

    String runtimeApi = env.get("AWS_LAMBDA_RUNTIME_API");
    String initErrorUrl =
        MessageFormat.format(LAMBDA_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);

    String error =
        MessageFormat.format(ERROR_RESPONSE_TEMPLATE, "Invocation Error", "RuntimeError");

    try {
      HttpClient.post(initErrorUrl, error);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Invoke Lambda Runtime.
   *
   * @param env {@link Map} - System environment parameters
   * @throws IOException IOException
   */
  public static void invoke(final Map<String, String> env) throws IOException {
    String method = null;
    String handlerName = env.get("_HANDLER");
    int pos = handlerName.indexOf("::");
    if (pos > 0) {
      method = handlerName.substring(pos + 2);
      handlerName = handlerName.substring(0, pos);
    }

    Class<?> clazz = null;

    try {
      clazz = Class.forName(handlerName);
    } catch (Exception e) {
      LambdaRuntime.handleInitError(env, e);
    }

    if (clazz != null) {
      invokeClass(env, clazz, method);
    }
  }

  /**
   * Handle Lambda Request.
   *
   * @param env {@link Map}
   * @param clazz {@link Class}
   * @param method {@link String}
   * @throws IOException Request Failed to get Lambda Runtime Event
   */
  private static void invokeClass(
      final Map<String, String> env, final Class<?> clazz, final String method) throws IOException {
    String runtimeApi = env.get("AWS_LAMBDA_RUNTIME_API");

    String runtimeUrl =
        MessageFormat.format(LAMBDA_RUNTIME_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE);

    // Main event loop
    while (true) {

      // Get next Lambda Event
      HttpResponse event = HttpClient.get(runtimeUrl);
      String requestId = event.getHeaderValue("Lambda-Runtime-Aws-Request-Id");

      try {
        // Invoke Handler Method
        Object handler = clazz.getConstructor().newInstance();
        String result = invokeLambdaRequestHandler(handler, method, requestId, event.getBody());

        // Post the results of Handler Invocation
        String invocationUrl =
            MessageFormat.format(
                LAMBDA_INVOCATION_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);
        HttpClient.post(invocationUrl, result);

      } catch (Exception e) {
        handleInvocationException(env, requestId, e);
      }

      if ("true".equals(env.getOrDefault("GRAALVM_MAIN_LOOP_STOP", "false"))) {
        break;
      }
    }
  }

  /**
   * Invoke Lambda method.
   *
   * @param handler {@link Object}
   * @param methodName {@link String}
   * @param requestId {@link String}
   * @param payload {@link String}
   * @return {@link String}
   * @throws Exception Exception
   */
  private static String invokeLambdaRequestHandler(
      final Object handler, final String methodName, final String requestId, final String payload)
      throws Exception {

    String value = null;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Context context = new LambdaContext(requestId);

    if (methodName != null) {
      value = invokeMethod(handler, methodName, payload, context);
    } else if (handler instanceof RequestHandler) {
      value = invokeMethod(handler, "handleRequest", payload, context);
    } else if (handler instanceof RequestStreamHandler) {

      value = invokeRequestStreamHandler((RequestStreamHandler) handler, payload, output, context);

    } else {
      throw new UnsupportedOperationException(
          "Unsupported handler: " + handler.getClass().getName());
    }

    return value;
  }

  /**
   * Invoke {@link RequestHandler}.
   *
   * @param object {@link Object}
   * @param methodName {@link String}
   * @param payload {@link String}
   * @param context {@link Context}
   * @return {@link String}
   * @throws InvocationTargetException InvocationTargetException
   * @throws IllegalArgumentException IllegalArgumentException
   * @throws IllegalAccessException IllegalAccessException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static String invokeMethod(
      final Object object, final String methodName, final String payload, final Context context)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    String val = "";
    Method method = findRequestHandlerMethod(object.getClass(), methodName);
    Parameter parameter = method.getParameters()[0];

    Gson gson = new GsonBuilder().create();
    Object input = gson.fromJson(payload, parameter.getType());

    Object value = null;
    if (methodName == null && object instanceof RequestHandler) {
      value = ((RequestHandler) object).handleRequest(input, context);
    } else {
      value = method.invoke(object, input, context);
    }

    Class<?> valueClass = value != null ? value.getClass() : null;

    if (valueClass != null && value != null) {

      if (String.class.equals(valueClass)) {
        val = value.toString();
      } else {
        val = gson.toJson(value);
      }
    }

    return val;
  }

  /**
   * Invoke {@link RequestStreamHandler}.
   *
   * @param handler {@link RequestStreamHandler}
   * @param payload {@link String}
   * @param output {@link ByteArrayOutputStream}
   * @param context {@link Context}
   * @return {@link String}
   * @throws IOException IOException
   */
  private static String invokeRequestStreamHandler(
      final RequestStreamHandler handler,
      final String payload,
      final ByteArrayOutputStream output,
      final Context context)
      throws IOException {

    InputStream input = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));
    handler.handleRequest(input, output, context);

    return new String(output.toByteArray(), StandardCharsets.UTF_8);
  }

  /**
   * Main Linux for Lambda.
   *
   * @param args String[]
   * @throws IOException IOException
   */
  public static void main(final String[] args) throws IOException {
    invoke(System.getenv());
  }
}
