
# Lambda Runtime Graalvm
=====================================

Library for building AWS Lambda Functions using Graalvm. 


Installation
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


Maven Installation
------------------

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

Gradle Installation
-------------------

Add the following to your build.gradle

```
   implementation group: 'com.formkiq', name: 'lambda-runtime-graalvm', version:'VERSION'
   <!-- Replace VERSION with the version you want to use -->
```

Licensing
=========

This library is licensed under the Apache Software License, version 2.0.
