
# Lambda Runtime Graalvm

Lambda Runtime Graalvm is a Java Library that makes it easy to convert [AWS Lambdas](https://aws.amazon.com/lambda/) writting in [Java](https://www.java.com) using [Graalvm](https://www.graalvm.org/). 

Benefits of using [Graalvm](https://www.graalvm.org/) over [Java](https://www.java.com):
 * Much faster startup time (seconds to milliseconds)
 * Much lower memory usage

## Components
 * [lambda-runtime](https://github.com/formkiq/lambda-runtime-graalvm/tree/master/lambda-runtime) - AWS Lambda Graalvm Runtime
 * [graalvm-annotations](https://github.com/formkiq/lambda-runtime-graalvm/tree/master/graalvm-annotations) - Graalvm Annotations
 * [graalvm-annotations-processors](https://github.com/formkiq/lambda-runtime-graalvm/tree/master/graalvm-annotations-processors) - Graalvm Annotations Processors
 * [gradle-plugin](https://github.com/formkiq/gradle-plugin/tree/master/samples) - Gradle Plugin
 * [samples](https://github.com/formkiq/lambda-runtime-graalvm/tree/master/samples) - Graalvm Sample Code

## Tutorial

 https://blog.formkiq.com/tutorials/aws-lambda-graalvm


## Building
------------------
Clone the repository, and use

```
    ./gradlew build
```

to build the jar. Add the jar, located at a path similar to:

```
    build/libs/formkiq-java-client-<version>.jar
```

If you would like a copy of the javadocs, use

```
    ./gradlew javadoc
```

the javadoc documentation is located at path

```
    build/docs/javadoc/
```


## Maven Installation

Add the following to your pom.xml

```xml
    <!-- FormKiQ Client Library Dependency -->
    <dependency>
        <groupId>com.formkiq</groupId>
        <artifactId>lambda-runtime-graalvm</artifactId>
        <version>VERSION</version>
        <!-- Replace VERSION with the version you want to use -->
    </dependency>
```

## Gradle Installation

Add the following to your build.gradle

```
   implementation group: 'com.formkiq', name: 'lambda-runtime-graalvm', version:'VERSION'
   <!-- Replace VERSION with the version you want to use -->
```

## Licensing

This library is licensed under the Apache Software License, version 2.0.
