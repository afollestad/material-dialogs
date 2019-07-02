# Color

# Table of Contents - Color

1. [Gradle Dependency](#gradle-dependency)
2. [Color Choosers](#color-choosers)
    1. [Basics](#basics)
    2. [Sub Colors](#sub-colors) 

## Gradle Dependency

[ ![Color](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Acolor/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Acolor/_latestVersion)

The `color` module contains extensions to the core module, such as a color chooser.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:color:3.1.0'
}
```

## Color Choosers

### Basics

Color choosers show a simple grid of colors.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/color_chooser.png" width="250px" />

```kotlin
val colors = intArrayOf(RED, GREEN, BLUE)

MaterialDialog(this).show {
  title(R.string.colors)
  colorChooser(colors) { dialog, color ->
      // Use color integer
  }
  positiveButton(R.string.select)
}
```

You can specify an initial selection, which is just a color integer:

```kotlin
val colors = intArrayOf(RED, GREEN, BLUE)

MaterialDialog(this).show {
  title(R.string.colors)
  colorChooser(colors, initialSelection = BLUE) { dialog, color ->
      // Use color integer
  }
  positiveButton(R.string.select)
}
```

### Sub Colors

You can specify sub-colors, which are a level down from each top level color. The size of the top 
level array must match the size of the sub-colors array.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/color_chooser_sub.png" width="250px" />

```kotlin
val colors = intArrayOf(RED, GREEN, BLUE) // size = 3

val subColors = listOf( // size = 3
  intArrayOf(LIGHT_RED, RED, DARK_RED, WHITE),
  intArrayOf(LIGHT_GREEN, GREEN, DARK_GREEN, GRAY),
  intArrayOf(LIGHT_BLUE, BLUE, DARK_BLUE, BLACK)
)

MaterialDialog(this).show {
  title(R.string.colors)
  colorChooser(colors, subColors = subColors) { dialog, color ->
      // Use color integer
  }
  positiveButton(R.string.select)
}
```

### ARGB Selection

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/custom_argb.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  title(R.string.colors)
  colorChooser(
      colors = colors, 
      subColors = subColors,
      allowCustomArgb = true,
      showAlphaSelector = true
  ) { dialog, color ->
      // Use color integer
  }
  positiveButton(R.string.select)
}
```

Omitting `showAlphaSelector` will hide the alpha (transparency) selector.