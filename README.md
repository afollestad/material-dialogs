# Material Dialogs

#### [View Releases and Changelogs](https://github.com/afollestad/material-dialogs/releases)

[![Android CI](https://github.com/afollestad/material-dialogs/workflows/Android%20CI/badge.svg)](https://github.com/afollestad/material-dialogs/actions?query=workflow%3A%22Android+CI%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0a4acc30a9ce440087f7688735359bb8)](https://www.codacy.com/app/drummeraidan_50/material-dialogs?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/material-dialogs&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

![Showcase](https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/showcase4.png)

# Modules

The core module is the fundamental module that you need in order to use this library. The others 
are extensions to core.

Please note that since Material Dialogs 2.x.x, this library only supports Kotlin. The latest Java version is `0.9.6.0` and can be found [here](README_OLD.md). Note that 0.9.6.0 is unsupported, bugs & improvements will not be made to that version.

## Core

[ ![Core](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/core?label=core&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/core)

#### [Core Tutorial and Samples](documentation/CORE.md)

The `core` module contains everything you need to get started with the library. It contains all
core and normal-use functionality.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/basic_with_buttons.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:core:3.3.0'
}
```

## Input

[ ![Input](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/input?label=input&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/input)

#### [Input Tutorial and Samples](documentation/INPUT.md)
 
The `input` module contains extensions to the core module, such as a text input dialog.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/input.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:input:3.3.0'
}
```
 
## Files

[ ![Files](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/files?label=files&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/files)

#### [Files Tutorial and Samples](documentation/FILES.md)

The `files` module contains extensions to the core module, such as a file and folder chooser.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/file_chooser.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:files:3.3.0'
}
```

## Color

[ ![Color](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/color?label=color&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/color)

#### [Color Tutorial and Samples](documentation/COLOR.md)

The `color` module contains extensions to the core module, such as a color chooser.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/color_chooser.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:color:3.3.0'
}
```

## DateTime

[ ![DateTime](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/datetime?label=datetime&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/datetime)

#### [DateTime Tutorial and Samples](documentation/DATETIME.md)

The `datetime` module contains extensions to make date, time, and date-time picker dialogs.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/datetimepicker.png" width="500px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:datetime:3.3.0'
}
```

## Bottom Sheets

[ ![Bottom Sheets](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/bottomsheets?label=bottomsheets&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/bottomsheets)

#### [Bottom Sheets Tutorial and Samples](documentation/BOTTOMSHEETS.md)

The `bottomsheets` module contains extensions to turn modal dialogs into bottom sheets, among 
other functionality like showing a grid of items. Be sure to checkout the sample project for this,
too!

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/main/art/bottomsheet_customview.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:bottomsheets:3.3.0'
}
```

## Lifecycle

[ ![Lifecycle](https://img.shields.io/maven-central/v/com.afollestad.material-dialogs/lifecycle?label=lifecycle&style=for-the-badge) ](https://repo1.maven.org/maven2/com/afollestad/material-dialogs/lifecycle)

#### [Lifecycle Tutorial and Samples](documentation/LIFECYCLE.md)

The `lifecycle` module contains extensions to make dialogs work with AndroidX lifecycles.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:lifecycle:3.3.0'
}
```
