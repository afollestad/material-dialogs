# Material Dialogs

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


# Table of Contents - Core

1. [Gradle Dependency](#gradle-dependency)
2. [Changes in Version 2](#changes-in-version-2)
3. [Basics](#basics)
4. [Action Buttons](#action-buttons)
5. [Adding an Icon](#adding-an-icon)
6. [Callbacks](#callbacks)
7. [Kotlin Extras](#kotlin-extras)
8. [Dismissing](#dismissing)
9. [Lists](#lists)
    1. [Plain](#plain)
    2. [Single Choice](#single-choice)
    3. [Multiple Choice](#multiple-choice)
    4. [Custom Adapters](#custom-adapters)
10. [Checkbox Prompts](#checkbox-prompts)
11. [Custom Views](#custom-views)
12. [Miscellaneous](#miscellaneous)
14. [Theming](#theming)
    1. [Light and Dark](#light-and-dark)
    2. [Background Color](#background-color)
    3. [Corner Radius](#corner-radius)
    4. [Text Color](#text-color)
    5. [Fonts](#fonts)

# Table of Contents - Input

1. [Gradle Dependency](#gradle-dependency-1)
2. [Text Input](#text-input)
    1. [Basics](#basics-1)
    2. [Hints and Prefill](#hints-and-prefill)
    3. [Input Types](#input-types)
    4. [Max Length](#max-length)
    5. [Custom Validation](#custom-validation)

---

# Core

## Gradle Dependency


The `core` module contains everything you need to get started with the library. It contains all
core and normal-use functionality.

```gradle
dependencies {

    implementation 'com.github.georgehargreaves95:material-dialogs:2.0.0-beta5'
}

```

## Changes in Forked Version

In order to protect the integrity of my own projects I've forked and pared down the original MaterialDialogs library. The library isn't particularly intended for public 
use and isn't under active development.

## Basics

Here's a very basic example of creating and showing a dialog:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/basic.png" width="200px" />

```kotlin
MaterialDialog(this)
  .title(R.string.your_title)
  .message(R.string.your_message)
  .show()
```

`this` should be a `Context` which is attached to a window, like an `Activity`.

If you wanted to pass in literal strings instead of string resources:

```kotlin
MaterialDialog(this)
  .title(text = "Your Title")
  .message(text = "Your Message")
  .show()
```

Note that you can setup a dialog without immediately showing it, as well:

```kotlin
val dialog = MaterialDialog(this)
  .title(R.string.your_title)
  .message(R.string.your_message)
  
dialog.show()
```

## Action Buttons

There are simple methods for adding action buttons:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/basic_with_buttons.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  .positiveButton(R.string.agree)
  .negativeButton(R.string.disagree)
  .show()
```

You can use literal strings here as well:

```kotlin
MaterialDialog(this)
  ...
  .positiveButton(text = "Agree")
  .negativeButton(text = "Disagree")
  .show()
```

---

Listening for clicks on the buttons is as simple as adding a lambda to the end:

```kotlin
MaterialDialog(this)
  ...
  .positiveButton(R.string.agree) { dialog ->
    // Do something
  }
  .negativeButton(R.string.disagree) { dialog ->
    // Do something
  }
  .show()
```

If action buttons together are too long to fit in the dialog's width, they will be automatically
stacked:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/stacked_buttons.png" width="200px" />

## Adding an Icon

You can display an icon to the left of the title:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/icon.png" width="200px" />

```kotlin
MaterialDialog(this)
  .title(R.string.your_title)
  .icon(R.drawable.your_icon)
  .show()
```

You can pass a Drawable instance as well:

```kotlin
val myDrawable: Drawable = // ...
MaterialDialog(this)
  ...
  .icon(drawable = myDrawable)
  .show()
```

## Callbacks

There are a few lifecycle callbacks you can hook into:

```kotlin
MaterialDialog(this)
  ...
  .onPreShow { dialog -> }
  .onShow { dialog -> }
  .onDismiss { dialog -> }
  .onCancel { dialog -> }
  .show()
```

## Kotlin Extras

There's a cool way you can setup and show dialogs in a simple call with Kotlin:

```kotlin
MaterialDialog(this).show {
  title(R.string.your_title)
  message(R.string.your_message)
  positiveButton(R.string.agree) { }
  negativeButton(R.string.disagree) { }
}
```

## Dismissing 

Dismissing a dialog closes it, it's just a simple method inherited from the parent `Dialog` class:

```kotlin
val dialog: MaterialDialog = // ...

dialog.dismiss()
```

---

You can prevent a dialog from being canceled, meaning it has to be explictly dismissed with an 
action button or a call to the method above.

```kotlin
MaterialDialog(this).show {
  cancelable(false)  // calls setCancelable on the underlying dialog
  cancelOnTouchOutside(false)  // calls setCanceledOnTouchOutside on the underlying dialog
}
```

## Lists

### Plain

You can show lists using the `listItems` extension on `MaterialDialog`:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/basic_list.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  .listItems(R.array.socialNetworks)
  .show()
```

You can pass a literal string array too:

```kotlin
val myItems = listOf("Hello", "World")

MaterialDialog(this)
  ...
  .listItems(items = myItems)
  .show()
```

To get item selection events, just append a lambda:

```kotlin
MaterialDialog(this)
  ...
  .listItems(R.array.socialNetworks) { dialog, index, text ->
    // Invoked when the user taps an item
  }
  .show()
```

### Single Choice

You can show single choice (radio button) lists using the `listItemsSingleChoice` extension 
on `MaterialDialog`:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/single_choice_list.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  .listItemsSingleChoice(R.array.my_items) { _, index, text ->
    // 
  }
  .show()
```

You can pass a literal string array too:

```kotlin
val myItems = listOf("Hello", "World")

MaterialDialog(this)
  ...
  .listItemsSingleChoice(items = myItems)
  .show()
```

---

If you want an option to be selected when the dialog opens, you can pass an `initialSelection` index):

```kotlin
MaterialDialog(this)
  ...
  .listItemsSingleChoice(R.array.my_items, initialSelection = 1)
  .show()
```

To get item selection events, just append a lambda:

```kotlin
MaterialDialog(this)
  ...
  .listItemsSingleChoice(R.array.my_items) { dialog, index, text ->
    // Invoked when the user selects an item
  }
  .show()
```

Without action buttons, the selection callback is invoked immediately when the user taps an item. If
you add a positive action button...

```kotlin
MaterialDialog(this)
  ...
  .listItemsSingleChoice(R.array.my_items) { dialog, index, text ->
    // Invoked when the user selects an item
  }
  .positiveButton(R.string.select)
  .show()
```

...then the callback isn't invoked until the user selects an item *and* taps the positive action 
button. You can override that behavior using the `waitForPositiveButton` argument.

An added bonus, you can disable items from being selected/unselected:

```kotlin
val indices = intArrayOf(0, 2)

MaterialDialog(this)
  ...
  .listItemsSingleChoice(R.array.my_items, disabledIndices = indices)
  .show()
```

---

There are methods you can use in a built dialog to modify checked states:

```kotlin
val dialog: MaterialDialog = // ...

dialog.checkItem(index)

dialog.uncheckItem(index)

dialog.toggleItemChecked(index)

val checked: Boolean = dialog.isItemChecked(index)
```

### Multiple Choice

You can show multiple choice (checkbox) lists using the `listItemsMultiChoice` extension on `MaterialDialog`:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/multi_choice_list.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  .listItemsMultiChoice(R.array.my_items) { _, index, text ->
    // 
  }
  .show()
```

You can pass a literal string array too:

```kotlin
val myItems = listOf("Hello", "World")

MaterialDialog(this)
  ...
  .listItemsMultiChoice(items = myItems)
  .show()
```

---

If you want option(s) to be selected when the dialog opens, you can pass an `initialSelection` index):

```kotlin
val indices = intArrayOf(1, 3)

MaterialDialog(this)
  ...
  .listItemsMultiChoice(R.array.my_items, initialSelection = indices)
  .show()
```

To get item selection events, just append a lambda:

```kotlin
MaterialDialog(this)
  ...
  .listItemsMultiChoice(R.array.my_items) { dialog, indices, items ->
    // Invoked when the user selects an item
  }
  .show()
```

Without action buttons, the selection callback is invoked immediately when the user taps an item. If
you add a positive action button...

```kotlin
MaterialDialog(this)
  ...
  .listItemsMultiChoice(R.array.my_items) { dialog, indices, items ->
    // Invoked when the user selects an item
  }
  .positiveButton(R.string.select)
  .show()
```

...then the callback isn't invoked until the user select one or more items *and* taps the positive 
action button. You can override that behavior using the `waitForPositiveButton` argument.

An added bonus, you can disable items from being selected/unselected:

```kotlin
val indices = intArrayOf(0, 2)

MaterialDialog(this)
  ...
  .listItemsMultiChoice(R.array.my_items, disabledIndices = indices)
  .show()
```

---

There are methods you can use in a built dialog to modify checked states:

```kotlin
val dialog: MaterialDialog = // ...
val indices: IntArray = // ...

dialog.checkItems(indices)

dialog.uncheckItems(indices)

dialog.toggleItemsChecked(indices)

dialog.checkAllItems()

dialog.uncheckAllItems()

dialog.toggleAllItemsChecked()

val checked: Boolean = dialog.isItemChecked(index)
```

### Custom Adapters 

If you want to customize lists to use your own views, you need to use a custom adapter.

```kotlin
val adapter: RecyclerView.Adapter<*> = // some sort of adapter implementation...

MaterialDialog(this)
  .customListAdapter(adapter)
  .show()
```

You can retrieve your adapter again later from the dialog instance:

```kotlin
val dialog: MaterialDialog = // ...

val adapter: RecyclerView.Adapter<*> = dialog.getListAdapter()
```

You can also retrieve the `RecyclerView` that the adapter is hosted in:

```kotlin
val dialog: MaterialDialog = // ...

val recyclerView: RecyclerView = dialog.getRecyclerView()
```

## Checkbox Prompts

Checkbox prompts can be used together with any other dialog type, it gets shown in the same view
which shows the action buttons.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/checkbox_prompt.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  checkBoxPrompt(R.string.your_label) { checked ->
    // Check box was checked or unchecked
  }
  .show()
```

You can pass a literal string for the label too:

```kotlin
MaterialDialog(this)
  ...
  .checkBoxPrompt(text = "Hello, World")
  .show()
```

---

You can also append a lambda which gets invoked when the checkbox is checked or unchecked:

```kotlin
MaterialDialog(this)
  ...
  .checkBoxPrompt(text = "Hello, World") { checked -> }
  .show()
```

If you only care about the checkbox state when the positive action button is pressed:

```kotlin
MaterialDialog(this)
  ...
  .checkBoxPrompt(R.string.your_label)
  .positiveButton(R.string.button_text) { dialog ->
      val isChecked = dialog.isCheckPromptChecked()
      // do something
  }
  .show()
```

## Custom Views

A lot of the included extensions use custom views, such as the color chooser dialog. There's also 
a simple example in the sample project.

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/custom_view.png" width="200px" /> 

```kotlin
MaterialDialog(this)
  ...
  .customView(R.layout.my_custom_view)
  .show()
```

You can also pass a literal view:

```kotlin
val myView: View = // ...

MaterialDialog(this)
  ...
  .customView(view = myView)
  .show()
```

If your custom view may be taller than the dialog, you'll want to make it scrollable:

```kotlin
MaterialDialog(this)
  ...
  .customView(R.layout.my_custom_view, scrollable = true)
  .show()
```

For later access, you can use `dialog.getCustomView()`:

```kotlin
val dialog = MaterialDialog(this)
  ...
  .customView(R.layout.my_custom_view, scrollable = true)
  
val customView = dialog.getCustomView()
// Use the view instance, e.g. to set values or setup listeners
  
dialog.show()
```

## Miscellaneous

There are little details which are easy to miss. For an example, auto dismiss controls whether pressing 
the action buttons or tapping a list item will automatically dismiss the dialog or not. By default, 
it's turned on. You can disable it:

```kotlin
MaterialDialog(this)
  ...
  .noAutoDismiss()
  .show()
```

## Theming

Google's newer mindset with Material Theming (vs the 2014 mindset) is flexible. If you take their 
["Crane example"](https://material.io/design/components/dialogs.html#theming), you see that they 
change fonts, corner rounding, etc. 

### Light and Dark

Light and dark theming is automatic based on your app's theme (basically whether `android:textColorPrimary` 
is more light or more dark):

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/lightanddarkthemes.jpg" width="400px" />

### Background Color

Material Dialogs uses the value of the `colorBackgroundFloating` attribute in your Activity theme 
for the background color of dialogs. You can also use the `md_background_color` attribute in your 
theme, which will take precedence.

### Corner Radius

Corner radius is the rounding of dialog corners:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/cornerradius.png" width="200px" />

it can be changed with an attribute in your app theme. It defaults to 2dp:

```xml
<style name="AppTheme.Custom" parent="Theme.AppCompat">

  <item name="md_corner_radius">16dp</item>
    
</style>
```

### Text Color

By default, `android:textColorPrimary` and `android:textColorSecondary` attributes from your Activity
theme are used for the title and content colors of dialogs. If you wish to override that, there 
are two attributes provided:

```xml
<style name="AppTheme.Custom" parent="Theme.AppCompat">

  <item name="md_color_title">@color/your_color</item>
  <item name="md_color_content">@color/your_color</item>
    
</style>
```

### Fonts

This library supports using custom fonts, powered by the Support libraries `ResourcesCompat` class. 
With raw font files or XML font files in your `/res/font` folder, you can use them in Material Dialogs 
using attributes in your app's theme.

```xml
<style name="AppTheme.Custom" parent="Theme.AppCompat">

  <item name="md_font_title">@font/your_font</item>
  <item name="md_font_body">@font/your_font</item>
  <item name="md_font_button">@font/your_font</item>
    
</style>
```

See the "Custom Theme" example in the sample project (open the overflow menu for the theme switcher).

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/customtheme.png" width="200px" />

---

# Input

## Gradle Dependency

[ ![Input](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Ainput/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Ainput/_latestVersion)

The `input` module contains extensions to the core module, such as a text input dialog.

```gradle
dependencies {
	
    implementation 'com.afollestad.material-dialogs:input:2.0.0-beta5'
}
```

If Gradle is unable to resolve, add this to your repositories:

```gradle
maven { url "https://dl.bintray.com/drummer-aidan/maven/" }
```

## Text Input

### Basics

You can setup an input dialog using the `input` extension on `MaterialDialog`:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/input.png" width="200px" />

```kotlin
MaterialDialog(this)
  ...
  .input()
  .positiveButton(R.string.submit)
  .show()
```

With a setup input dialog, you can retrieve the input field:

```kotlin
val dialog: MaterialDialog = // ...
val inputField: EditText = dialog.getInputField()
```

---

You can append a lambda to receive a callback when the positive action button is pressed with 
text entered: 

```kotlin
MaterialDialog(this)
  ...
  .input { dialog, text ->
      // Text submitted with the action button
  }
  .positiveButton(R.string.submit)
  .show()
```

If you set `waitForPositiveButton` to false, the callback is invoked every time the text field is
modified:

```kotlin
MaterialDialog(this)
  ...
  .input(waitForPositiveButton = false) { dialog, text -> 
      // Text changed
  }
  .positiveButton(R.string.done)
  .show()
```

### Hints and Prefill

You can set a hint to the input field, which is the gray faded text shown when the field is empty:

```kotlin
MaterialDialog(this)
  .input(hintRes = R.string.hint_text)
  .show()
```

A literal string can be used as well:

```kotlin
MaterialDialog(this)
  .input(hint = "Your Hint Text")
  .show()
```

---

You can also prefill the input field:

```kotlin
MaterialDialog(this)
  .input(prefillRes = R.string.prefill_text)
  .show()
```

A literal string can be used as well:

```kotlin
MaterialDialog(this)
  .input(prefill = "Prefilled text")
  .show()
```

### Input Types

You can apply input types to the input field, which modifies the keyboard type when the field is 
focused on. This is just taken right from the Android framework, the input type gets applied 
directly to the underlying `EditText`:

```kotlin
val type = InputType.TYPE_CLASS_TEXT or 
  InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
  
MaterialDialog(this)
  .input(inputType = type)
  .show()
```

### Max Length

You can set a max length which makes a character counter visible, and disables the positive action 
button if the input length goes over that:

<img src="https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/input_max_length.png" width="200px" />

```kotlin
MaterialDialog(this)
  .input(maxLength = 8)
  .positiveButton(R.string.submit)
  .show()
```

### Custom Validation

You can do custom validation using the input listener. This example enforces that the input
starts with the letter 'a':

```kotlin
MaterialDialog(this)
  .input(waitForPositiveButton = false) { dialog, text ->
    val inputField = dialog.getInputField()!!
    val isValid = text.startsWith("a", true)
    
    inputField.error = if (isValid) null else "Must start with an 'a'!"
    dialog.setActionButtonEnabled(POSITIVE, isValid)
  }
  .positiveButton(R.string.submit)
  .show()
```

---
