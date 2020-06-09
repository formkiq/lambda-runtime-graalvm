/**
 * Copyright [2020] FormKiQ Inc. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.formkiq.lambda.runtime.graalvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * {@link HttpHandler} for the Lambda Error Response.
 *
 */
public class ErrorResponseHandler implements HttpHandler {

  /** Http Response. */
  private String response = null;

  @Override
  public void handle(final HttpExchange httpExchange) throws IOException {

    InputStream is = httpExchange.getRequestBody();

    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(
        new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    }

    this.response = textBuilder.toString();

    handleResponse(httpExchange, "");
  }

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
   * Get Http Response.
   * 
   * @return {@link String}
   */
  public String getResponse() {
    return this.response;
  }
}
