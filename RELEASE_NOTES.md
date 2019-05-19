3.0.0-beta1

* Fixed status bar and navigation appearance for bottom sheet dialogs.
* We're nearing a "stable" release.

### 3.0.0-alpha4

Minor API change: The `message(...)` function no longer provides `html` and `lineSpacingMultiplier` 
as parameters. Instead, it works like this:

```kotlin
MaterialDialog(this).show {
  ...
  message(R.string.htmlContent) {
      html() // or...
      html { toast("Clicked link: $it") }
      
      lineSpacing(1.4f)
      messageTextView.doSomething() // you can act directly on the TextView
  }
}
```

### 3.0.0-alpha3

* The `BottomSheet()` dialog behavior now accepts an optional `LayoutMode` parameter, which you can use 
to instruct the bottom sheet to be expandable to the screen height or limit itself to wrap the content 
of its content. See the updated Bottom Sheets documentation.
* Added a `setPeekHeight(...)` extension method for `MaterialDialog` that you can use to set the 
default peek height and animate peek height changes.