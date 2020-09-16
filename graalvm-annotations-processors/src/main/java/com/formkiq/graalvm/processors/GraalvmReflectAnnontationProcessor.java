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
package com.formkiq.graalvm.processors;

import com.formkiq.graalvm.annotations.Reflectable;
import com.formkiq.graalvm.annotations.ReflectableClass;
import com.formkiq.graalvm.annotations.ReflectableClasses;
import com.formkiq.graalvm.annotations.ReflectableField;
import com.formkiq.graalvm.annotations.ReflectableImport;
import com.formkiq.graalvm.annotations.ReflectableMethod;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/** Processes {@link Reflectable} {@link ReflectableImport} Annotations. */
@AutoService(Processor.class)
@SupportedAnnotationTypes({
  "com.formkiq.graalvm.annotations.Reflectable",
  "com.formkiq.graalvm.annotations.ReflectableImport",
  "com.formkiq.graalvm.annotations.ReflectableClasses",
  "com.formkiq.graalvm.annotations.ReflectableClass"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class GraalvmReflectAnnontationProcessor extends AbstractProcessor {

  /** Log {@link Level}. */
  private static final Level LOGLEVEL = Level.INFO;
  /** {@link Logger}. */
  private static final Logger LOGGER =
      Logger.getLogger(GraalvmReflectAnnontationProcessor.class.getName());
  /** {@link List} of {@link Reflect}. */
  private Map<String, Reflect> reflects = new HashMap<>();
  /** {@link Gson}. */
  private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  /**
   * Get ClassName from {@link Element}.
   *
   * @param element {@link Element}
   * @return {@link String}s
   */
  private String getClassName(final Element element) {
    String className = null;

    switch (element.getKind()) {
      case FIELD:
      case CONSTRUCTOR:
      case METHOD:
        className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
        break;
      case CLASS:
        className = ((TypeElement) element).getQualifiedName().toString();
        break;
      default:
        break;
    }

    return className;
  }

  /**
   * Get {@link Reflect}.
   *
   * @param className {@link String}
   * @return {@link Reflect}
   */
  private Reflect getReflect(final String className) {

    Reflect reflect = reflects.getOrDefault(className, null);

    if (reflect == null) {
      reflect = new Reflect();
      reflects.put(className, reflect);
      LOGGER.log(LOGLEVEL, "creating new Element");
    } else {
      LOGGER.log(LOGLEVEL, "appending to previous Element");
    }

    reflect.name(className);

    return reflect;
  }

  @Override
  public boolean process(
      final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

    if (roundEnv.processingOver()) {

      writeOutput();

    } else {

      processingReflectableImports(roundEnv);
      processingReflectable(roundEnv);
      processReflectableClasses(roundEnv);
    }

    return true;
  }

  /**
   * Process Class.
   *
   * @param reflect {@link Reflect}
   * @param reflectable {@link Reflectable}
   * @return {@link Reflect}
   */
  private Reflect processClass(final Reflect reflect, final Reflectable reflectable) {

    LOGGER.log(LOGLEVEL, "processClass " + reflect.name());
    reflect
        .allDeclaredConstructors(reflectable.allDeclaredConstructors())
        .allDeclaredFields(reflectable.allDeclaredFields())
        .allDeclaredMethods(reflectable.allDeclaredMethods())
        .allPublicConstructors(reflectable.allPublicConstructors())
        .allPublicFields(reflectable.allPublicFields())
        .allPublicMethods(reflectable.allPublicMethods());

    return reflect;
  }

  /**
   * Process Class.
   *
   * @param reflect {@link Reflect}
   * @param reflectable {@link ReflectableClass}
   * @return {@link Reflect}
   */
  private Reflect processClass(final Reflect reflect, final ReflectableClass reflectable) {

    LOGGER.log(LOGLEVEL, "processClass " + reflect.name());
    reflect
        .allDeclaredConstructors(reflectable.allDeclaredConstructors())
        .allDeclaredFields(reflectable.allDeclaredFields())
        .allDeclaredMethods(reflectable.allDeclaredMethods())
        .allPublicConstructors(reflectable.allPublicConstructors())
        .allPublicFields(reflectable.allPublicFields())
        .allPublicMethods(reflectable.allPublicMethods());

    return reflect;
  }

  /**
   * Process Imported Class using {@link Class}.
   *
   * @param clazz {@link Class}
   */
  private void processImportedClass(final String clazz) {
    try {
      Class<?> forName = Class.forName(clazz);
      Reflectable reflectable = forName.getAnnotation(Reflectable.class);
      Reflect reflect = getReflect(clazz);

      if (reflectable != null) {
        processClass(reflect, reflectable);
      }

      for (Field field : forName.getDeclaredFields()) {
        Reflectable reflection = field.getAnnotation(Reflectable.class);
        if (reflection != null) {
          LOGGER.log(LOGLEVEL, "adding Field " + field.getName() + " to " + clazz);
          reflect.addField(field.getName(), reflectable.allowWrite());
        }
      }

      for (Method method : forName.getMethods()) {
        Reflectable reflection = method.getAnnotation(Reflectable.class);
        if (reflection != null) {
          List<String> parameterTypes =
              Arrays.asList(method.getParameters()).stream()
                  .map(p -> p.getParameterizedType().getTypeName())
                  .collect(Collectors.toList());

          LOGGER.log(LOGLEVEL, "adding Method " + method.getName() + " to " + clazz);
          reflect.addMethod(method.getName(), parameterTypes);
        }
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Process Imported Classes.
   *
   * @param element {@link Element}
   */
  private void processImportedClasses(final Element element) {

    List<String> classNames = findClasses(element, "classes");
    for (String clazz : classNames) {
      LOGGER.log(LOGLEVEL, "processing ImportedClass " + clazz);
      processImportedClass(clazz);
    }
  }

  /**
   * Find Class Names using {@link AnnotationMirror}.
   *
   * @param element {@link Element}
   * @param key {@link String}
   * @return {@link List} {@link String}
   */
  private List<String> findClasses(final Element element, final String key) {

    List<String> classNames = new ArrayList<>();

    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();

    for (AnnotationMirror annotationMirror : annotationMirrors) {

      Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues =
          annotationMirror.getElementValues();

      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
          elementValues.entrySet()) {

        String simpleNameKey = entry.getKey().getSimpleName().toString();
        Object value = entry.getValue().getValue();

        if (key.equals(simpleNameKey)) {

          @SuppressWarnings("unchecked")
          List<? extends AnnotationValue> typeMirrors = (List<? extends AnnotationValue>) value;

          for (AnnotationValue val : typeMirrors) {
            String clazz = ((TypeMirror) val.getValue()).toString();

            LOGGER.log(LOGLEVEL, "processing ImportedClass " + clazz);
            processImportedClass(clazz);
          }
        }
      }
    }

    return classNames;
  }

  /**
   * Process Importing of Files.
   *
   * @param element {@link Element}
   */
  @SuppressWarnings("unchecked")
  private void processImportFiles(final Element element) {
    ReflectableImport[] reflectImports = element.getAnnotationsByType(ReflectableImport.class);

    for (ReflectableImport reflectImport : reflectImports) {

      for (String file : reflectImport.files()) {

        if (file.length() > 0) {
          try {

            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(file);
            String data = Files.readString(new File(resource.getFile()).toPath());
            List<Map<String, Object>> list = gson.fromJson(data, List.class);

            for (Map<String, Object> map : list) {
              Reflect reflect = getReflect(map.get("name").toString());
              reflect.data(map);
            }

          } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
          }
        }
      }
    }
  }

  /**
   * Processing classes with 'ReflectableClasses' and 'ReflectableClass' annotation.
   *
   * @param roundEnv {@link RoundEnvironment}
   */
  private void processReflectableClasses(final RoundEnvironment roundEnv) {

    for (Element element : roundEnv.getElementsAnnotatedWith(ReflectableClasses.class)) {

      String className = getClassName(element);
      LOGGER.log(LOGLEVEL, "processing 'ReflectableClasses' annotation on class " + className);

      ReflectableClasses[] reflectables = element.getAnnotationsByType(ReflectableClasses.class);

      for (ReflectableClasses reflectable : reflectables) {

        ReflectableClass[] classes = reflectable.value();
        for (ReflectableClass clazz : classes) {
          processReflectableClass(clazz);
        }
      }
    }

    for (Element element : roundEnv.getElementsAnnotatedWith(ReflectableClass.class)) {
      String className = getClassName(element);
      LOGGER.log(LOGLEVEL, "processing 'ReflectableClasses' annotation on class " + className);

      ReflectableClass[] reflectables = element.getAnnotationsByType(ReflectableClass.class);
      for (ReflectableClass clazz : reflectables) {
        processReflectableClass(clazz);
      }
    }
  }

  /**
   * Processing classes with 'ReflectableClass' annotation.
   *
   * @param reflectable {@link ReflectableClass}
   */
  private void processReflectableClass(final ReflectableClass reflectable) {

    String className = null;
    try {
      reflectable.className();
    } catch (MirroredTypeException e) {
      TypeMirror typeMirror = e.getTypeMirror();
      className = asTypeElement(typeMirror).getQualifiedName().toString();
    }

    Reflect reflect = getReflect(className);
    reflect = processClass(reflect, reflectable);

    for (ReflectableField field : reflectable.fields()) {
      LOGGER.log(LOGLEVEL, "adding Field " + field.name() + " to " + className);
      reflect.addField(field.name(), field.allowWrite());
    }

    for (ReflectableMethod method : reflectable.methods()) {
      LOGGER.log(LOGLEVEL, "adding Method " + method.name() + " to " + className);
      reflect.addMethod(method.name(), Arrays.asList(method.parameterTypes()));
    }
  }

  private TypeElement asTypeElement(final TypeMirror typeMirror) {
    Types typeUtils = this.processingEnv.getTypeUtils();
    return (TypeElement) typeUtils.asElement(typeMirror);
  }

  /**
   * Processing classes with 'Reflectable' annotation.
   *
   * @param roundEnv {@link RoundEnvironment}
   */
  private void processingReflectable(final RoundEnvironment roundEnv) {

    for (Element element : roundEnv.getElementsAnnotatedWith(Reflectable.class)) {

      String className = getClassName(element);
      LOGGER.log(LOGLEVEL, "processing 'Reflectable' annotation on class " + className);

      Reflectable[] reflectables = element.getAnnotationsByType(Reflectable.class);

      Reflect reflect = getReflect(className);
      for (Reflectable reflectable : reflectables) {

        switch (element.getKind()) {
          case FIELD:
            String fieldName = element.getSimpleName().toString();
            LOGGER.log(LOGLEVEL, "adding Field " + fieldName + " to " + className);
            reflect.addField(fieldName, reflectable.allowWrite());
            break;
          case CONSTRUCTOR:
          case METHOD:
            String methodName = element.getSimpleName().toString();

            List<String> parameterTypes =
                ((ExecutableElement) element)
                    .getParameters().stream()
                        .map(param -> param.asType().toString())
                        .collect(Collectors.toList());

            LOGGER.log(LOGLEVEL, "adding Method " + methodName + " to " + className);
            reflect.addMethod(methodName, parameterTypes);

            break;

          case CLASS:
            reflect = processClass(reflect, reflectable);
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * Load Reflectable Imports.
   *
   * @param roundEnv {@link RoundEnvironment}s
   */
  private void processingReflectableImports(final RoundEnvironment roundEnv) {

    for (Element element : roundEnv.getElementsAnnotatedWith(ReflectableImport.class)) {
      processImportedClasses(element);
      processImportFiles(element);
    }
  }

  /** Write Output File. */
  private void writeOutput() {

    try {

      FileObject file =
          processingEnv
              .getFiler()
              .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/graal/reflect.json");

      List<Map<String, Object>> data =
          this.reflects.values().stream().map(r -> r.data()).collect(Collectors.toList());

      try (Writer w = new OutputStreamWriter(file.openOutputStream(), "UTF-8")) {
        w.write(gson.toJson(data));
      }

    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
