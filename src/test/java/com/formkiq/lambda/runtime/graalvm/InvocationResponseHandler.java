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

/** {@link ExpectationResponseCallback} for the Lambda Invocation Response. */
public class InvocationResponseHandler implements ExpectationResponseCallback {

  /** Http Response. */
  private String response = null;

  /**
   * Get Http Response.
   *
   * @return {@link String}
   */
  public String getResponse() {
    return this.response;
  }

  @Override
  public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
    this.response = httpRequest.getBodyAsString();
    if (this.response == null) {
      this.response = "";
    }

    final int statusCode = 200;
    return HttpResponse.response(this.response).withStatusCode(Integer.valueOf(statusCode));
  }
}
