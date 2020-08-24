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

/**
 * An annotation that when applid to a class add the class to Graalvm's list of Reflection classes
 * to process. Based on https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Reflectable {

  /**
   * Include all declared constructors to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allDeclaredConstructors() default false;

  /**
   * Include all declared fields to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allDeclaredFields() default true;

  /**
   * Include all declared methods to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allDeclaredMethods() default true;

  /**
   * Include all public constructors to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allPublicConstructors() default true;

  /**
   * Include all public fields to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allPublicFields() default true;

  /**
   * Include all public methods to a {@link ElementType#TYPE}.
   *
   * @return boolean
   */
  boolean allPublicMethods() default true;

  /**
   * Set allowWrite on Field.
   *
   * @return boolean
   */
  boolean allowWrite() default false;
}
