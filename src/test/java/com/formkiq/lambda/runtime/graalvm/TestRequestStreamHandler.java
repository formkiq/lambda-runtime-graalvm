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
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/** Test {@link RequestStreamHandler} and returns a {@link String}. */
public class TestRequestStreamHandler implements RequestStreamHandler {

  @Override
  public void handleRequest(
      final InputStream input, final OutputStream output, final Context context)
      throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
    writer.write("test data result");
    writer.close();
  }
}
