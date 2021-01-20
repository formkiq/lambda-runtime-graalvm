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

import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/** {@link ExpectationResponseCallback} for the Lambda Next Response. */
public class InvocationNextHandler implements ExpectationResponseCallback {

  /** Lambda Request Id. */
  private static final String REQUEST_ID = "testrequestid";
  /** Lambda Request Id. */
  private static final String TRACE_ID = "testtraceid";

  /** Response Content. */
  private String responseContent = "test";

  /**
   * Set Response Content.
   *
   * @param content {@link String}
   */
  public void setResponseContent(final String content) {
    this.responseContent = content;
  }

  @Override
  public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
    final int statusCode = 200;
    return HttpResponse.response()
        .withHeader("Lambda-Runtime-Aws-Request-Id", REQUEST_ID)
        .withHeader("Lambda-Runtime-Trace-Id", TRACE_ID)
        .withBody(responseContent)
        .withStatusCode(Integer.valueOf(statusCode));
  }
}
