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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/** HttpResponse. */
public class HttpResponse {

  /** HTTP Status Code. */
  private int statusCode;

  /** HTTP Body. */
  private String body;

  /** HTTP Headers. */
  private Map<String, List<String>> headers;

  /** constructor. */
  public HttpResponse() {
    this.headers = new HashMap<>();
  }

  /**
   * constructor.
   *
   * @param status int
   */
  public HttpResponse(final int status) {
    this();
    setStatusCode(status);
  }

  /**
   * Get HTTP Status Code.
   *
   * @return int
   */
  public int getStatusCode() {
    return this.statusCode;
  }

  /**
   * Set HTTP Status Code.
   *
   * @param status int
   */
  public void setStatusCode(final int status) {
    this.statusCode = status;
  }

  /**
   * Get HTTP Headers.
   *
   * @return {@link Map}
   */
  public Map<String, List<String>> getHeaders() {
    return this.headers;
  }

  /**
   * Set HTTP Headers.
   *
   * @param httpHeaders {@link Map}
   */
  public void setHeaders(final Map<String, List<String>> httpHeaders) {
    this.headers = httpHeaders;
  }

  /**
   * Add HTTP Header.
   *
   * @param key {@link String}
   * @param values {@link List}
   */
  public void addHeader(final String key, final List<String> values) {
    this.headers.put(key, values);
  }

  /**
   * Get HTTP Body.
   *
   * @return {@link String}
   */
  public String getBody() {
    return this.body;
  }

  /**
   * Set HTTP Body.
   *
   * @param httpbody {@link String}
   */
  public void setBody(final String httpbody) {
    this.body = httpbody;
  }

  /**
   * Get Header Values.
   *
   * @param key {@link String}
   * @return {@link List}
   */
  public List<String> getHeaderValues(final String key) {
    Optional<Entry<String, List<String>>> o =
        this.headers.entrySet().stream().filter(e -> key.equalsIgnoreCase(e.getKey())).findFirst();
    return o.isPresent() ? o.get().getValue() : Collections.emptyList();
  }

  /**
   * Get Header Value.
   *
   * @param key {@link String}
   * @return {@link List}
   */
  public String getHeaderValue(final String key) {
    List<String> values = getHeaderValues(key);
    return !values.isEmpty() ? values.get(0) : null;
  }
}
