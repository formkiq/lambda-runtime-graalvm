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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Http Service using the build in Java {@link HttpURLConnection} library. */
public class HttpClient {

  /**
   * Send 'Get' request.
   *
   * @param url {@link String}
   * @return {@link HttpResponse}
   * @throws IOException IOException
   */
  public static HttpResponse get(final String url) throws IOException {
    URL u = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
    conn.setRequestMethod("GET");

    return buildResponse(conn);
  }

  /**
   * Build {@link HttpResponse} from {@link HttpURLConnection}.
   *
   * @param conn {@link HttpURLConnection}
   * @return {@link HttpResponse}
   * @throws IOException IOException
   */
  private static HttpResponse buildResponse(final HttpURLConnection conn) throws IOException {

    HttpResponse response = new HttpResponse(conn.getResponseCode());

    conn.getHeaderFields()
        .entrySet()
        .forEach(
            e -> {
              if (e.getKey() != null && e.getValue() != null) {
                response.addHeader(e.getKey(), e.getValue());
              }
            });

    StringBuilder sb = new StringBuilder();
    BufferedReader br =
        new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

    String line = null;
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }

    br.close();

    response.setBody(sb.toString());

    return response;
  }

  /**
   * Send Http POST.
   *
   * @param url {@link String}
   * @param body {@link String}
   * @return {@link HttpResponse}
   * @throws IOException IOException
   */
  public static HttpResponse post(final String url, final String body) throws IOException {

    URL u = new URL(url);

    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");

    OutputStreamWriter response =
        new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);

    response.write(body);
    response.flush();
    response.close();
    conn.connect();

    return new HttpResponse(conn.getResponseCode());
  }
}
