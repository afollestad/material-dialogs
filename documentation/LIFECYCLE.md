# Lifecycle

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [Usage](#usage)


## Gradle Dependency

[ ![Lifecycle](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/lifecycle?label=lifecycle&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/lifecycle)

The `lifecycle` module contains extensions to make dialogs work with AndroidX lifecycles.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:lifecycle:3.2.1'
}
```

## Usage

```kotlin
MaterialDialog(this).show {
  ...
  lifecycleOwner(owner)
}
```

When the given lifecycle owner is destroyed, the dialog is automatically dismissed. Lifecycle 
owners include Activities and Fragments from AndroidX, along with any class that implements the
`LifecycleOwner` interface.
