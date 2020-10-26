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

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.io.IOException;

/** Implementation of {@link LambdaLogger}. */
public class LambdaLoggerSystemOut implements LambdaLogger {

  @Override
  public void log(final String message) {
    System.out.println(message);
    System.out.flush();
  }

  @Override
  public void log(final byte[] message) {
    try {
      System.out.write(message);
    } catch (IOException e) {
      // NOTE: When actually running on AWS Lambda, an IOException would never happen
      e.printStackTrace();
    }
  }
}
