# Material Dialogs

#### [View Releases and Changelogs](https://github.com/afollestad/material-dialogs/releases)

[![Build Status](https://travis-ci.org/afollestad/material-dialogs.svg)](https://travis-ci.org/afollestad/material-dialogs)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0a4acc30a9ce440087f7688735359bb8)](https://www.codacy.com/app/drummeraidan_50/material-dialogs?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/material-dialogs&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

![Showcase](https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/showcase4.png)

# Modules

The core module is the fundamental module that you need in order to use this library. The others 
are extensions to core.

## Core

[ ![Core](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Acore/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Acore/_latestVersion)

#### [Core Tutorial and Samples](documentation/CORE.md)

The `core` module contains everything you need to get started with the library. It contains all
core and normal-use functionality.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/basic_with_buttons.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:core:3.1.0'
}
```

## Input

[ ![Input](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Ainput/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Ainput/_latestVersion)

#### [Input Tutorial and Samples](documentation/INPUT.md)
 
The `input` module contains extensions to the core module, such as a text input dialog.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/input.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:input:3.1.0'
}
```
 
## Files

[ ![Files](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Afiles/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Afiles/_latestVersion)

#### [Files Tutorial and Samples](documentation/FILES.md)

The `files` module contains extensions to the core module, such as a file and folder chooser.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_chooser.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:files:3.1.0'
}
```

## Color

[ ![Color](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Acolor/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Acolor/_latestVersion)

#### [Color Tutorial and Samples](documentation/COLOR.md)

The `color` module contains extensions to the core module, such as a color chooser.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/color_chooser.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:color:3.1.0'
}
```

## DateTime

[ ![DateTime](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Adatetime/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Adatetime/_latestVersion)

#### [DateTime Tutorial and Samples](documentation/DATETIME.md)

The `datetime` module contains extensions to make date, time, and date-time picker dialogs.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/datetimepicker.png" width="500px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:datetime:3.1.0'
}
```

## Bottom Sheets

[ ![Bottom Sheets](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Abottomsheets/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Abottomsheets/_latestVersion)

#### [Bottom Sheets Tutorial and Samples](documentation/BOTTOMSHEETS.md)

The `bottomsheets` module contains extensions to turn modal dialogs into bottom sheets, among 
other functionality like showing a grid of items. Be sure to checkout the sample project for this,
too!

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/bottomsheet_customview.png" width="250px" />

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:bottomsheets:3.1.0'
}
```

## Lifecycle

[ ![Lifecycle](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Alifecycle/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Alifecycle/_latestVersion)

#### [Lifecycle Tutorial and Samples](documentation/LIFECYCLE.md)

The `lifecycle` module contains extensions to make dialogs work with AndroidX lifecycles.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:lifecycle:3.1.0'
}
```
