# Bottom Sheets

## Table of Contents

1. [Gradle Dependency](#gradle-dependency)
2. [Usage](#usage)
3. [Item Grids](#item-grids)


## Gradle Dependency

[ ![Bottom Sheets](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Abottomsheets/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Abottomsheets/_latestVersion)

The `bottomsheets` module contains extensions to turn modal dialogs into bottom sheets, among 
other functionality like showing a grid of items.

```gradle
dependencies {
  ...
  implementation 'com.afollestad.material-dialogs:bottomsheets:3.0.0-alpha1'
}
```

## Usage

Making a dialog a bottom sheet is as simple as passing a constructed instance of `BottomSheet` 
as the second parameter to `MaterialDialog`'s constructor.

```kotlin
MaterialDialog(this, BottomSheet()).show {
  ...
}
```

## Item Grids

Since it's common to show a grid of items in a bottom sheet, this module contains a method to do 
that.

```kotlin
val items = listOf(
    BasicGridItem(R.drawable.some_icon, "One"),
    BasicGridItem(R.drawable.another_icon, "Two"),
    BasicGridItem(R.drawable.hello_world, "Three"),
    BasicGridItem(R.drawable.material_dialogs, "Four")
)

MaterialDialog(this, BottomSheet()).show {
  ...
  gridItems(items) { _, index, item ->
    toast("Selected item ${item.title} at index $index")
  }
}
```

Note that `gridItems` can take a list of anything that inherits from the `GridItem` interface, 
if you wish to pass a custom item type. You just need to override the `title` value along with the 
`populateIcon(ImageView)` function.

---

There a few extra parameters that you can provide, most of which are equivelent to what you can 
provide to `listItems`. `customGridWidth` is an optional integer resource that allows you to set a 
width for the grid - you can have different widths for different resource configurations (tablet, 
landscape, etc.)

```kotlin
gridItems(
  items: List<IT : GridItem>,
  @IntegerRes customGridWidth: Int? = null,
  disabledIndices: IntArray? = null,
  waitForPositiveButton: Boolean = true,
  selection: GridItemListener<IT> = null
): MaterialDialog
```
