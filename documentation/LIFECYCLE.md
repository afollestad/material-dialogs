# Lifecycle

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [Usage](#usage)


## Gradle Dependency

[ ![Lifecycle](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Alifecycle/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Alifecycle/_latestVersion)

The `lifecycle` module contains extensions to make dialogs work with AndroidX lifecycles.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:lifecycle:3.1.0'
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
