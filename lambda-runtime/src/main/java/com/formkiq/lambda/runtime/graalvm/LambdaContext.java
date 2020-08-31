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

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/** Implementation of {@link Context}. */
public class LambdaContext implements Context {

  /** AWS Request Id. */
  private String awsRequestId;
  /** {@link LambdaLogger}. */
  private LambdaLogger logger = new LambdaLoggerSystemOut();

  /**
   * constructor.
   *
   * @param requestId {@link String}
   */
  public LambdaContext(final String requestId) {
    this.awsRequestId = requestId;
  }

  @Override
  public String getAwsRequestId() {
    return this.awsRequestId;
  }

  @Override
  public String getLogGroupName() {
    return System.getenv("AWS_LAMBDA_LOG_GROUP_NAME");
  }

  @Override
  public String getLogStreamName() {
    return System.getenv("AWS_LAMBDA_LOG_STREAM_NAME");
  }

  @Override
  public String getFunctionName() {
    return System.getenv("AWS_LAMBDA_FUNCTION_NAME");
  }

  @Override
  public String getFunctionVersion() {
    return System.getenv("AWS_LAMBDA_FUNCTION_VERSION");
  }

  @Override
  public String getInvokedFunctionArn() {
    throw new UnsupportedOperationException();
  }

  @Override
  public CognitoIdentity getIdentity() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClientContext getClientContext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getRemainingTimeInMillis() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMemoryLimitInMB() {
    return Integer.parseInt(System.getenv("AWS_LAMBDA_FUNCTION_MEMORY_SIZE"));
  }

  @Override
  public LambdaLogger getLogger() {
    return this.logger;
  }
}
