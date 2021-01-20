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
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/** Test {@link RequestStreamHandler} and returns a {@link String}. */
public class TestRequestApiGatewayProxyHandler
    implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Override
  public APIGatewayProxyResponseEvent handleRequest(
      final APIGatewayProxyRequestEvent input, final Context context) {
    APIGatewayProxyResponseEvent e = new APIGatewayProxyResponseEvent();
    e.setBody(input.getBody());
    return e;
  }
}
