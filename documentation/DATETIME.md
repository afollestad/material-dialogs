# DateTime

## Table of Contents

1. [Gradle Dependency](#gradle-dependency-4)
2. [Date](#date)
3. [Time](#time)
4. [DateTime](#datetime)

## Gradle Dependency

[ ![DateTime](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Adatetime/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Adatetime/_latestVersion)

The `datetime` module contains extensions to make date, time, and date-time picker dialogs.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:datetime:3.1.0'
}
```

## Date

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/datepicker.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  ...
  datePicker { dialog, date ->
    // Use date (Calendar)
  }
}
```

You can optionally provide `minDate` and `currentDate` parameters as well.

## Time

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/timepicker.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  ...
  timePicker { dialog, time ->
    // Use time (Calendar)
  }
}
```

You can optionally provide `currentTime` and `show24HoursView` parameters as well.

## DateTime

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/datetimepicker.png" width="400px" />

```kotlin
MaterialDialog(this).show {
  ...
  dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
    // Use dateTime (Calendar)
  }
}
```

You can optionally provide `minDateTime`, `currentDateTime`, `show24HoursView`, 
and `requireFutureDateTime` parameters as well.