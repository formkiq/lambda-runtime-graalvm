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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** {@link HttpHandler} for the Lambda Next Response. */
public class InvocationNextHandler implements HttpHandler {

  /** Lambda Request Id. */
  private static final String REQUEST_ID = "testrequestid";

  /** Response Content. */
  private String responseContent = "test";

  @Override
  public void handle(final HttpExchange httpExchange) throws IOException {

    Headers responseHeaders = httpExchange.getResponseHeaders();
    responseHeaders.add("Lambda-Runtime-Aws-Request-Id", REQUEST_ID);

    handleResponse(httpExchange, getResponseContent());
  }

  /**
   * Handle building response.
   *
   * @param httpExchange {@link HttpExchange}
   * @param body {@link String}
   * @throws IOException IOException
   */
  private static void handleResponse(final HttpExchange httpExchange, final String body)
      throws IOException {

    final int statusCode = 200;
    OutputStream outputStream = httpExchange.getResponseBody();

    httpExchange.sendResponseHeaders(statusCode, body.length());

    outputStream.write(body.getBytes(StandardCharsets.UTF_8));
    outputStream.flush();
    outputStream.close();
  }

  /**
   * Get Response Content.
   *
   * @return {@link String}
   */
  public String getResponseContent() {
    return this.responseContent;
  }

  /**
   * Set Response Content.
   *
   * @param content {@link String}
   */
  public void setResponseContent(final String content) {
    this.responseContent = content;
  }
}
