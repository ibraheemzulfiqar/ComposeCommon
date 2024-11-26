<h1 align="center">ComposeCommon</h1><br>

[![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Jitpack](https://jitpack.io/v/ibraheemzulfiqar/ComposeCommon.svg)](https://jitpack.io/#ibraheemzulfiqar/ComposeCommon)

A collection of reusable Jetpack Compose components and extensions for internal use.

## Implementation

To include `ComposeCommon` in your project, add the JitPack repository to your build file:

```kotlin
maven { setUrl("https://jitpack.io") }
```

### Add the Dependency

#### Include All Modules
If you want to include all components provided by `ComposeCommon`, use the following dependency:

```kotlin
implementation("com.github.ibraheemzulfiqar:ComposeCommon:v0.6.0") // Replace with the latest version
```

#### Include Specific Modules
If you only need specific functionality, you can include individual modules:

```kotlin
// Pattern module
implementation("com.github.ibraheemzulfiqar.ComposeCommon:pattern:v0.6.0")

// Rating Bar module
implementation("com.github.ibraheemzulfiqar.ComposeCommon:ratingbar:v0.6.0")

// Voyager Extensions module
implementation("com.github.ibraheemzulfiqar.ComposeCommon:voyager-extensions:v0.6.0")
```

[![Jitpack](https://jitpack.io/v/ibraheemzulfiqar/ComposeCommon.svg)](https://jitpack.io/#ibraheemzulfiqar/ComposeCommon)
