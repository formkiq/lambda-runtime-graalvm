/**
 * Copyright [2020] FormKiQ Inc. Licensed under the Apache License, ersion 2.0 (the "License"); you
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
package com.formkiq.graalvm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** An annotation to import existing Graal Config files. */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ReflectableImport {

  /**
   * Include all files to a {@link ElementType#TYPE}.
   *
   * @return String[]
   */
  String[] files() default "";

  /**
   * Include all classes to a {@link ElementType#TYPE}.
   *
   * @return Class[]
   */
  Class<?>[] classes() default {};
}
