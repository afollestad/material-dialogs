# Files

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [File Choosers](#file-choosers)
    1. [Basics](#basics)
    2. [Filter](#filter)
    3. [Empty Text](#empty-text)
    4. [Folder Creation](#folder-creation)
3. [Folder Choosers](#folder-choosers)
    1. [Basics](#basics-1)
    2. [Filter](#filter-1)
    3. [Empty Text](#empty-text-1)
    4. [Folder Creation](#folder-creation-1)

## Gradle Dependency

[ ![Files](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Afiles/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Afiles/_latestVersion)

The `files` module contains extensions to the core module, such as a file and folder chooser.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:files:3.1.0'
}
```

## File Choosers

### Basics

**Note:** File choosers require your app to have permission to `READ_EXTERNAL_STORAGE`, otherwise 
directory listings will come back empty.

You create file choosers using the `fileChooser` extension on `MaterialDialog`:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_chooser.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  fileChooser { dialog, file ->
      // File selected
  }
}
```

It shows all files and folders, starting in the external storage directory. Tapping a file invokes 
the callback and dismisses the dialog.

You can change the directory which is listed initially:

```kotlin
val initialFolder = File(getExternalStorageDirectory(), "Download")

MaterialDialog(this).show {
  fileChooser(initialDirectory = initialFolder) { dialog, file ->
      // File selected
  }
}
```

**If a positive action button exists, tapping a file will select it, but the callback isn't invoked 
until the positive action button is pressed.**

### Filter

A filter can be applied to only show the files and directories you wish to show:

```kotlin
// show ALL folders, and files that start with the letter 'a'
val myFilter: FileFilter = { it.isDirectory || it.nameWithoutExtension.startsWith("a", true) }

MaterialDialog(this).show {
  fileChooser(filter = myFilter) { dialog, file ->
      // File selected
  }
}
```

### Empty Text

Empty text is shown when a folder has no contents. You can configure the empty text label:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_emptytext.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  fileChooser(emptyTextRes = R.string.custom_label) { dialog, file ->
      // File selected
  }
}
```

### Folder Creation

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_folder_creation.png" width="250px" />

You can allow your users to create folders. 

```kotlin
MaterialDialog(this).show {
  fileChooser(
      allowFolderCreation = true,
      folderCreationLabel = R.string.new_folder // optional as well
  ) { dialog, file -> 
      // File selected
  }
}
```

This "New Folder" option is only show in directories which are writable.

## Folder Choosers

**Note:** Folder choosers require your app to have permission to `READ_EXTERNAL_STORAGE`, otherwise 
directory listings will come back empty.

Folder choosers are basically the same as file choosers, with a few minor differences: 1) only folders 
are shown, even when a custom filter is applied. 2) the selection callback is never invoked on a 
item click, it only gets invoked with the currently viewed folder when the positive action button 
is pressed.

### Basics

```kotlin
MaterialDialog(this).show {
  folderChooser { dialog, folder ->
      // Folder selected
  }
}
```

### Filter

You can apply a filter like you can with the file chooser.

```kotlin
// show only folders that start with the letter 'a'
val myFilter: FileFilter = { it.name.startsWith("a", true) }

MaterialDialog(this).show {
  folderChooser(filter = myFilter) { dialog, file ->
      // Folder selected
  }
}
``` 

### Empty Text

Empty text is shown when a folder has no contents. You can configure the empty text label:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_emptytext.png" width="250px" />

```kotlin
MaterialDialog(this).show {
  folderChooser(emptyTextRes = R.string.custom_label) { dialog, file ->
      // File selected
  }
}
```

### Folder Creation

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/file_folder_creation.png" width="250px" />

You can allow your users to create folders. 

```kotlin
MaterialDialog(this).show {
  folderChooser(
      allowFolderCreation = true,
      folderCreationLabel = R.string.new_folder // optional as well
  ) { dialog, file -> 
      // File selected
  }
}
```

This "New Folder" option is only show in directories which are writable.