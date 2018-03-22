# Material Dialogs

[ ![Core](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Acore/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Acore/_latestVersion)
[ ![Commons](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs%3Acommons/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs%3Acommons/_latestVersion)
[![Build Status](https://travis-ci.org/afollestad/material-dialogs.svg)](https://travis-ci.org/afollestad/material-dialogs)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0a4acc30a9ce440087f7688735359bb8)](https://www.codacy.com/app/drummeraidan_50/material-dialogs?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=afollestad/material-dialogs&amp;utm_campaign=Badge_Grade)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/afollestad/material-dialogs/blob/master/LICENSE.txt)

![Screenshots](https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/readmeshowcase.png)

# Table of Contents (Core)

1. [Sample Project](https://github.com/afollestad/material-dialogs#sample-project)
2. [Gradle Dependency](https://github.com/afollestad/material-dialogs#gradle-dependency)
    1. [Repository](https://github.com/afollestad/material-dialogs#repository)
    2. [Core](https://github.com/afollestad/material-dialogs#core)
    3. [Commons](https://github.com/afollestad/material-dialogs#commons)
3. [What's New](https://github.com/afollestad/material-dialogs#whats-new)
4. [Basic Dialog](https://github.com/afollestad/material-dialogs#basic-dialog)
5. [Dismissing Dialogs](https://github.com/afollestad/material-dialogs#dismissing-dialogs)
6. [Displaying an Icon](https://github.com/afollestad/material-dialogs#displaying-an-icon)
7. [Stacked Action Buttons](https://github.com/afollestad/material-dialogs#stacked-action-buttons)
    1. [Stacking Behavior](https://github.com/afollestad/material-dialogs#stacking-behavior)
8. [Neutral Action Button](https://github.com/afollestad/material-dialogs#neutral-action-button)
9. [Callbacks](https://github.com/afollestad/material-dialogs#callbacks)
10. [CheckBox Prompts](https://github.com/afollestad/material-dialogs#checkbox-prompts)
11. [List Dialogs](https://github.com/afollestad/material-dialogs#list-dialogs)
12. [Single Choice List Dialogs](https://github.com/afollestad/material-dialogs#single-choice-list-dialogs)
    1. [Coloring Radio Buttons](https://github.com/afollestad/material-dialogs#coloring-radio-buttons)
13. [Multi Choice List Dialogs](https://github.com/afollestad/material-dialogs#multi-choice-list-dialogs)
    1. [Coloring Check Boxes](https://github.com/afollestad/material-dialogs#coloring-check-boxes)
14. [Assigning IDs to List Item Views](https://github.com/afollestad/material-dialogs#assigning-ids-to-list-item-views)
15. [Custom List Dialogs](https://github.com/afollestad/material-dialogs#custom-list-dialogs)
16. [Custom Views](https://github.com/afollestad/material-dialogs#custom-views)
    1. [Later Access](https://github.com/afollestad/material-dialogs#later-access)
17. [Typefaces](https://github.com/afollestad/material-dialogs#typefaces)
18. [Getting and Setting Action Buttons](https://github.com/afollestad/material-dialogs#getting-and-setting-action-buttons)
19. [Theming](https://github.com/afollestad/material-dialogs#theming)
    1. [Basics](https://github.com/afollestad/material-dialogs#basics)
    2. [Colors](https://github.com/afollestad/material-dialogs#colors)
    3. [Selectors](https://github.com/afollestad/material-dialogs#selectors)
    4. [Gravity](https://github.com/afollestad/material-dialogs#gravity)
    5. [Material Palette](https://github.com/afollestad/material-dialogs#material-palette)
20. [Global Theming](https://github.com/afollestad/material-dialogs#global-theming)
21. [Show, Cancel, and Dismiss Callbacks](https://github.com/afollestad/material-dialogs#show-cancel-and-dismiss-callbacks)
22. [Input Dialogs](https://github.com/afollestad/material-dialogs#input-dialogs)
    1. [Coloring the EditText](https://github.com/afollestad/material-dialogs#coloring-the-edittext)
    2. [Limiting Input Length](https://github.com/afollestad/material-dialogs#limiting-input-length)
    3. [Custom Invalidation](https://github.com/afollestad/material-dialogs#custom-invalidation)
23. [Progress Dialogs](https://github.com/afollestad/material-dialogs#progress-dialogs)
    1. [Proguard](https://github.com/afollestad/material-dialogs#proguard)
    2. [Indeterminate Progress Dialogs](https://github.com/afollestad/material-dialogs#indeterminate-progress-dialogs)
    3. [Determinate (Seek Bar) Progress Dialogs](https://github.com/afollestad/material-dialogs#determinate-seek-bar-progress-dialogs)
    4. [Make an Indeterminate Dialog Horizontal](https://github.com/afollestad/material-dialogs#make-an-indeterminate-dialog-horizontal)
    5. [Coloring the Progress Bar](https://github.com/afollestad/material-dialogs#coloring-the-progress-bar)
    6. [Custom Number and Progress Formats](https://github.com/afollestad/material-dialogs#custom-number-and-progress-formats)
24. [Tint Helper](https://github.com/afollestad/material-dialogs#tint-helper)
25. [Misc](https://github.com/afollestad/material-dialogs#misc)

# Table of Contents (Commons)

1. [Color Chooser Dialogs](https://github.com/afollestad/material-dialogs#color-chooser-dialogs)
    1. [Finding Visible Dialogs](https://github.com/afollestad/material-dialogs#finding-visible-dialogs)
    2. [User Color Input](https://github.com/afollestad/material-dialogs#user-color-input)
2. [File Selector Dialogs](https://github.com/afollestad/material-dialogs#file-selector-dialogs)
3. [Folder Selector Dialogs](https://github.com/afollestad/material-dialogs#folder-selector-dialogs)
4. [Preference Dialogs](https://github.com/afollestad/material-dialogs#preference-dialogs)
5. [Simple List Dialogs](https://github.com/afollestad/material-dialogs#simple-list-dialogs) 

------

# Sample Project

You can download the latest sample APK from this repo here: https://github.com/afollestad/material-dialogs/blob/master/sample/sample.apk

It's also on Google Play:

<a href="https://play.google.com/store/apps/details?id=com.afollestad.materialdialogssample" target="_blank">
  <img alt="Get it on Google Play"
       src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="60"/>
</a>

Having the sample project installed is a good way to be notified of new releases. Although Watching this 
repository will allow GitHub to email you whenever I publish a release.

---

# Gradle Dependency

### Repository

The Gradle dependency is available via [jCenter](https://bintray.com/drummer-aidan/maven/material-dialogs:core/view).
jCenter is the default Maven repository used by Android Studio.

The minimum API level supported by this library is API 14.

### Core

The *core* module contains all the major classes of this library, including `MaterialDialog`.
You can create basic, list, single/multi choice, progress, input, etc. dialogs with core.

```gradle
dependencies {
	// ... other dependencies here
    implementation 'com.afollestad.material-dialogs:core:0.9.6.0'
}
```

### Commons

The *commons* module contains extensions to the library that not everyone may need. This includes the
`ColorChooserDialog`, `FolderChooserDialog`, the Material `Preference` classes, and `MaterialSimpleListAdapter`/`MaterialSimpleListItem`.

```gradle
dependencies {
    // ... other dependencies here
    implementation 'com.afollestad.material-dialogs:commons:0.9.6.0'
}
```

It's likely that new extensions will be added to commons later.

---

# What's New

See the project's Releases page for a list of versions with their changelogs.

### [View Releases](https://github.com/afollestad/material-dialogs/releases)

If you Watch this repository, GitHub will send you an email every time I publish an update.

---

# Basic Dialog

First of all, note that `MaterialDialog` extends `DialogBase`, which extends `android.app.Dialog`.

Here's a basic example that mimics the dialog you see on Google's Material design guidelines
(here: http://www.google.com/design/spec/components/dialogs.html#dialogs-usage). Note that you can
always substitute literal strings and string resources for methods that take strings, the same goes
for color resources (e.g. `titleColor` and `titleColorRes`).

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree)
        .negativeText(R.string.disagree)
        .show();
```

**Your Activities need to inherit the AppCompat themes in order to work correctly with this library.**
The Material dialog will automatically match the `positiveColor` (which is used on the positive action
button) to the `colorAccent` attribute of your styles.xml theme.

If the content is long enough, it will become scrollable and a divider will be displayed above the action buttons.

---

# Dismissing Dialogs

I've had lots of issues asking how you dismiss a dialog. It works the same way that `AlertDialog` does, as
both `AlertDialog` and `MaterialDialog` are an instance of `android.app.Dialog` (which is where `dismiss()`
and `show()` come from). You cannot dismiss a dialog using it's `Builder`. You can only dismiss a
dialog using the dialog itself.

There's many ways you can get an instance of `MaterialDialog`. The two major ways are through the `show()` and `build()`
methods of `MaterialDialog.Builder`.

Through `show()`, which immediately shows the dialog and returns the visible dialog:

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree)
        .show();
```

Through `build()`, which only builds the dialog but doesn't show it until you say so:

```java
MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree);

MaterialDialog dialog = builder.build();
dialog.show();
```

Once the dialog is shown, you can dismiss it:

```java
dialog.dismiss();
```

There are other various places where the `MaterialDialog` instance is given, such as in some callbacks
 that are discussed in future sections below.

---

# Displaying an Icon

MaterialDialog supports the display of an icon just like the stock AlertDialog; it will go to the left of the title.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree)
        .icon(R.drawable.icon)
        .show();
```

You can limit the maximum size of the icon using the `limitIconToDefaultSize()`, `maxIconSize(int size)`,
 or `maxIconSizeRes(int sizeRes)` Builder methods.

---

# Stacked Action Buttons

If you have multiple action buttons that together are too wide to fit on one line, the dialog will stack the
buttons to be vertically oriented.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.longer_positive)
        .negativeText(R.string.negative)
        .show();
```

### Stacking Behavior

You can set stacking behavior from the `Builder`:

```java
new MaterialDialog.Builder(this)
    ...
    .stackingBehavior(StackingBehavior.ADAPTIVE)  // the default value
    .show();
```

---

# Neutral Action Button

You can specify neutral text in addition to the positive and negative text. It will show the neutral
action on the far left.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree)
        .negativeText(R.string.disagree)
        .neutralText(R.string.more_info)
        .show();
```

---

# Callbacks

**As of version 0.8.2.0, the `callback()` Builder method is deprecated in favor of the individual callback methods
  discussed below. Earlier versions will still require use of `ButtonCallback`.**

To know when the user selects an action button, you set callbacks:

```java
new MaterialDialog.Builder(this)
    .onPositive(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            // TODO
        }
    })
    .onNeutral(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            // TODO
        }
    })
    .onNegative(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            // TODO
        }
    })
    .onAny(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            // TODO
        }
    });
```

If you are listening for all three action buttons, you could just use `onAny()`. The `which` (`DialogAction`)
 parameter will tell you which button was pressed.

If `autoDismiss` is turned off, then you must manually dismiss the dialog in these callbacks. Auto dismiss is on by default.

---

# CheckBox Prompts

Checkbox prompts allow you to display a UI similar to what Android uses to ask for a permission on API 23+.

**Note:** you can use checkbox prompts with list dialogs and input dialogs, too.

```java
new MaterialDialog.Builder(this)
    .iconRes(R.drawable.ic_launcher)
    .limitIconToDefaultSize()
    .title(R.string.example_title)
    .positiveText(R.string.allow)
    .negativeText(R.string.deny)
    .onAny(new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(MaterialDialog dialog, DialogAction which) {
            showToast("Prompt checked? " + dialog.isPromptCheckBoxChecked());
        }
    })
    .checkBoxPromptRes(R.string.dont_ask_again, false, null)
    .show();
```

---

# List Dialogs

Creating a list dialog only requires passing in an array of strings. The callback (`itemsCallback`) is
also very simple.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .items(R.array.items)
        .itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
            }
        })
        .show();
```

If `autoDismiss` is turned off, then you must manually dismiss the dialog in the callback. Auto dismiss is on by default.
You can pass `positiveText()` or the other action buttons to the builder to force it to display the action buttons
below your list, however this is only useful in some specific cases.

---

# Single Choice List Dialogs

Single choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackSingleChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display radio buttons next to list items.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .items(R.array.items)
        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                /**
                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                 * returning false here won't allow the newly selected radio button to actually be selected.
                 **/
                return true;
            }
        })
        .positiveText(R.string.choose)
        .show();
```

If you want to preselect an item, pass an index 0 or greater in place of -1 in `itemsCallbackSingleChoice()`.
Later, you can update the selected index using `setSelectedIndex(int)` on the `MaterialDialog` instance,
if you're not using a custom adapter.

If you do not set a positive action button using `positiveText()`, the dialog will automatically call
the single choice callback when user presses the positive action button. The dialog will also dismiss itself,
unless auto dismiss is turned off.

If you make a call to `alwaysCallSingleChoiceCallback()`, the single choice callback will be called
every time the user selects/unselects an item.

## Coloring Radio Buttons

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 dialog's radio buttons. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 `widgetColorAttr()`, and `choiceWidgetColor()` method. Their names and parameter annotations make them self explanatory.

`widgetColor` is the same color that affects other UI elements. `choiceWidgetColor` is specific to
single and multiple choice dialogs, it only affects radio buttons and checkboxes. You provide a
`ColorStateList` rather than a single color which is used to generate a `ColorStateList`.

 Note that by default, radio buttons will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.
 
There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

---

# Multi Choice List Dialogs

Multiple choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackMultiChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display check boxes next to list items, and the callback can return multiple selections.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .items(R.array.items)
        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                /**
                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                 * returning false here won't allow the newly selected check box to actually be selected
                 * (or the newly unselected check box to be unchecked).
                 * See the limited multi choice dialog example in the sample project for details.
                 **/
                 return true;
            }
        })
        .positiveText(R.string.choose)
        .show();
```

If you want to preselect any items, pass an array of indices (resource or literal) in place of null
in `itemsCallbackMultiChoice()`. Later, you can update the selected indices using `setSelectedIndices(Integer[])`
on the `MaterialDialog` instance, if you're not using a custom adapter.

If you do not set a positive action button using `positiveText()`, the dialog will automatically call
the multi choice callback when user presses the positive action button. The dialog will also dismiss itself,
unless auto dismiss is turned off.

If you make a call to `alwaysCallMultiChoiceCallback()`, the multi choice callback will be called
every time the user selects/unselects an item.

## Coloring Check Boxes

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 dialog's check boxes. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 `widgetColorAttr()`, and `choiceWidgetColor()` method. Their names and parameter annotations make them self explanatory.

`widgetColor` is the same color that affects other UI elements. `choiceWidgetColor` is specific to
single and multiple choice dialogs, it only affects radio buttons and checkboxes. You provide a
`ColorStateList` rather than a single color which is used to generate a `ColorStateList`.

 Note that by default, radio buttons will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.
 
There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

---

# Assigning IDs to List Item Views

If you need to keep track of list items by ID rather than index, you can assign item IDs from an integer array:

```java
new MaterialDialog.Builder(this)
        .title(R.string.socialNetworks)
        .items(R.array.socialNetworks)
        .itemsIds(R.array.itemIds)
        .itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                Toast.makeText(Activity.this, which + ": " + text + ", ID = " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        })
        .show();
```

You can also pass a literal integer array (`int[]`) in place of an array resource ID.

---

# Custom List Dialogs

Like Android's native dialogs, you can also pass in your own adapter via `.adapter()` to customize
exactly how you want your list to work.

```java
new MaterialDialog.Builder(this)
        .title(R.string.socialNetworks)
         // second parameter is an optional layout manager. Must be a LinearLayoutManager or GridLayoutManager.
        .adapter(new ButtonItemAdapter(this, R.array.socialNetworks), null)
        .show();
```

**Note** that with newer releases, Material Dialogs no longer supports `ListView` and `ListAdapter`.
It's about time that everyone uses `RecyclerView`. **Your custom adapters will have to handle item click
events on their own; this library's classes and sample project have some good examples of how that is done correctly.**

If you need access to the `RecyclerView`, you can use the `MaterialDialog` instance:

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
        ...
        .build();

RecyclerView list = dialog.getRecyclerView();
// Do something with it

dialog.show();
```

Note that you don't need to be using a custom adapter in order to access the `RecyclerView`, it's there for single/multi choice dialogs, regular list dialogs, etc.

---

# Custom Views

Custom views are very easy to implement.

```java
boolean wrapInScrollView = true;
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .customView(R.layout.custom_view, wrapInScrollView)
        .positiveText(R.string.positive)
        .show();
```

If `wrapInScrollView` is true, then the library will place your custom view inside of a ScrollView for you.
This allows users to scroll your custom view if necessary (small screens, long content, etc.). However, there are cases
when you don't want that behavior. This mostly consists of cases when you'd have a ScrollView in your custom layout,
including ListViews, RecyclerViews, WebViews, GridViews, etc. The sample project contains examples of using both true
 and false for this parameter.

Your custom view will automatically have padding put around it when `wrapInScrollView` is true. Otherwise
you're responsible for using padding values that look good with your content.

## Later Access

If you need to access a View in the custom view after the dialog is built, you can use `getCustomView()` of
`MaterialDialog`. This is especially useful if you pass a layout resource to the `Builder`, the dialog will
handle the view inflation for you.

```java
MaterialDialog dialog = //... initialization via the builder ...
View view = dialog.getCustomView();
```

---

# Typefaces

If you want to use custom fonts, you can make a call to `typeface(String, String)` when
using the `Builder`. This will pull fonts from files in your project's `assets/fonts` folder. For example,
if you had `Roboto.ttf` and `Roboto-Light.ttf` in `/src/main/assets/fonts`, you would call `typeface("Roboto.ttf", "Roboto-Light.ttf")`.
This method will also handle recycling Typefaces via the `TypefaceHelper` which you can use in your own project to avoid duplicate 
allocations. The raw `typeface(Typeface, Typeface)` variation will not recycle typefaces, every call will allocate the Typeface again.

There's a global theming attribute available to automatically apply fonts to every Material Dialog in
your app, also.

---

# Getting and Setting Action Buttons

If you want to get a reference to one of the dialog action buttons after the dialog is built and shown (e.g. to enable or disable buttons):

```java
MaterialDialog dialog = //... initialization via the builder ...
View negative = dialog.getActionButton(DialogAction.NEGATIVE);
View neutral = dialog.getActionButton(DialogAction.NEUTRAL);
View positive = dialog.getActionButton(DialogAction.POSITIVE);
```

If you want to update the title of a dialog action button (you can pass a string resource ID in place of the literal string, too):

```java
MaterialDialog dialog = //... initialization via the builder ...
dialog.setActionButton(DialogAction.NEGATIVE, "New Title");
```

---

# Theming

Before Lollipop, theming AlertDialogs was basically impossible without using reflection and custom drawables.
Since KitKat, Android became more color neutral but AlertDialogs continued to use Holo Blue for the title and
title divider. Lollipop has improved even more, with no colors in the dialog by default other than the action
buttons. This library makes theming even easier.

## Basics

By default, Material Dialogs will apply a light theme or dark theme based on the `?android:textColorPrimary` 
attribute retrieved from the context creating the dialog. If the color is light (e.g. more white), it will
guess the Activity is using a dark theme and it will use the dialog's dark theme. Vice versa for the light theme. 
You can manually set the theme used from the `Builder#theme()` method:

```java
new MaterialDialog.Builder(this)
        .content("Hi")
        .theme(Theme.DARK)
        .show();
```

Or you can use the global theming attribute, which is discussed in the section below. Global theming 
avoids having to constantly call theme setters for every dialog you show.

## Colors

Pretty much every aspect of a dialog created with this library can be colored:

```java
new MaterialDialog.Builder(this)
        .titleColorRes(R.color.material_red_500)
        .contentColor(Color.WHITE) // notice no 'res' postfix for literal color
        .linkColorAttr(R.attr.my_link_color_attr)  // notice attr is used instead of none or res for attribute resolving
        .dividerColorRes(R.color.material_pink_500)
        .backgroundColorRes(R.color.material_blue_grey_800)
        .positiveColorRes(R.color.material_red_500)
        .neutralColorRes(R.color.material_red_500)
        .negativeColorRes(R.color.material_red_500)
        .widgetColorRes(R.color.material_red_500)
        .buttonRippleColorRes(R.color.material_red_500)
        .show();
```

The names are self explanatory for the most part. The `widgetColor` method, discussed in a few other
sections of this tutorial, applies to progress bars, check boxes, and radio buttons. Also note that 
each of these methods have 3 variations for setting a color directly, using color resources, and using 
color attributes.

## Selectors

Selectors are drawables that change state when pressed or focused.

```java
new MaterialDialog.Builder(this)
        .btnSelector(R.drawable.custom_btn_selector)
        .btnSelector(R.drawable.custom_btn_selector_primary, DialogAction.POSITIVE)
        .btnSelectorStacked(R.drawable.custom_btn_selector_stacked)
        .listSelector(R.drawable.custom_list_and_stackedbtn_selector)
        .show();
```

The first `btnSelector` line sets a selector drawable used for all action buttons. The second `btnSelector`
line overwrites the drawable used only for the positive button. This results in the positive button having
a different selector than the neutral and negative buttons. `btnSelectorStacked` sets a selector drawable
used when the buttons become stacked, either because there's not enough room to fit them all on one line,
or because you used `forceStacked(true)` on the `Builder`. `listSelector` is used for list items, when
you are NOT using a custom adapter.

***Note***: 

***An important note related to using custom action button selectors***: make sure your selector drawable references
inset drawables like the default ones do - this is important for correct action button padding.

## Gravity

It's probably unlikely you'd want to change gravity of elements in a dialog, but it's possible.

```java
new MaterialDialog.Builder(this)
        .titleGravity(GravityEnum.CENTER)
        .contentGravity(GravityEnum.CENTER)
        .btnStackedGravity(GravityEnum.START)
        .itemsGravity(GravityEnum.END)
        .buttonsGravity(GravityEnum.END)
        .show();
```

These are pretty self explanatory. `titleGravity` sets the gravity for the dialog title, `contentGravity`
sets the gravity for the dialog content, `btnStackedGravity` sets the gravity for stacked action buttons, 
`itemsGravity` sets the gravity for list items (when you're NOT using a custom adapter). 

For, `buttonsGravity` refer to this:

<table>
<tr>
<td><b>START (Default)</b></td>
<td>Neutral</td>
<td>Negative</td>
<td>Positive</td>
</tr>
<tr>
<td><b>CENTER</b></td>
<td>Negative</td>
<td>Neutral</td>
<td>Positive</td>
</tr>
<tr>
<td><b>END</b></td>
<td>Positive</td>
<td>Negative</td>
<td>Neutral</td>
</tr>
</table>

With no positive button, the negative button takes it's place except for with CENTER.

## Material Palette

To see colors that fit the Material design palette, see this page: http://www.google.com/design/spec/style/color.html#color-color-palette

---

# Global Theming

Most of the theming aspects discussed in the above section can be automatically applied to all dialogs
you show from an Activity which has a theme containing any of these attributes:

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">

    <!--
        All dialogs will default to Theme.DARK with this set to true.
    -->
    <item name="md_dark_theme">true</item>

    <!--
        This overrides the default dark or light dialog background color.
        Note that if you use a dark color here, you should set md_dark_theme to
        true so text and selectors look visible
    -->
    <item name="md_background_color">#37474F</item>

    <!--
        Applies an icon next to the title in all dialogs.
    -->
    <item name="md_icon">@drawable/ic_launcher</item>
  
    <!--
        Limit icon to a max size.
    -->
    <attr name="md_icon_max_size" format="dimension" />
    
    <!--
        Limit the icon to a default max size (48dp).
    -->
    <attr name="md_icon_limit_icon_to_default_size" format="boolean" />

    <!--
        By default, the title text color is derived from the
        ?android:textColorPrimary system attribute.
    -->
    <item name="md_title_color">#E91E63</item>


    <!--
        By default, the content text color is derived from the
        ?android:textColorSecondary system attribute.
    -->
    <item name="md_content_color">#9C27B0</item>

    <!--
        By default, the link color is derived from the colorAccent attribute 
        of AppCompat or android:colorAccent attribute of the Material theme.
    -->
    <item name="md_link_color">#673AB7</item>

    <!--
        By default, the positive action text color is derived
        from the colorAccent attribute of AppCompat or android:colorAccent
        attribute of the Material theme.
    -->
    <item name="md_positive_color">#673AB7</item>

    <!--
        By default, the neutral action text color is derived
        from the colorAccent attribute of AppCompat or android:colorAccent
        attribute of the Material theme.
    -->
    <item name="md_neutral_color">#673AB7</item>

    <!--
        By default, the negative action text color is derived
        from the colorAccent attribute of AppCompat or android:colorAccent
        attribute of the Material theme.
    -->
    <item name="md_negative_color">#673AB7</item>

    <!--
        By default, a progress dialog's progress bar, check boxes, and radio buttons 
        have a color that is derived from the colorAccent attribute of AppCompat or 
        android:colorAccent attribute of the Material theme.
    -->
    <item name="md_widget_color">#673AB7</item>

    <!--
        By default, the list item text color is black for the light
        theme and white for the dark theme.
    -->
    <item name="md_item_color">#9C27B0</item>

    <!--
        This overrides the color used for the top and bottom dividers used when
        content is scrollable
    -->
    <item name="md_divider_color">#E91E63</item>

    <!--
        This overrides the color used for the ripple displayed on action buttons (Lollipop and above).
        Defaults to the colorControlHighlight attribute from AppCompat OR the Material theme.
    -->
    <item name="md_btn_ripple_color">#E91E63</item>

    <!--
        This overrides the selector used on list items.
    -->
    <item name="md_list_selector">@drawable/selector</item>

    <!--
        This overrides the selector used on stacked action buttons.
    -->
    <item name="md_btn_stacked_selector">@drawable/selector</item>

    <!--
        This overrides the background selector used on the positive action button.
    -->
    <item name="md_btn_positive_selector">@drawable/selector</item>

    <!--
        This overrides the background selector used on the neutral action button.
    -->
    <item name="md_btn_neutral_selector">@drawable/selector</item>

    <!--
        This overrides the background selector used on the negative action button.
    -->
    <item name="md_btn_negative_selector">@drawable/selector</item>
    
    <!-- 
        This sets the gravity used while displaying the dialog title, defaults to start.
        Can be start, center, or end.
    -->
    <item name="md_title_gravity">start</item>
    
    <!-- 
        This sets the gravity used while displaying the dialog content, defaults to start.
        Can be start, center, or end.
    -->
    <item name="md_content_gravity">start</item>
    
    <!--
        This sets the gravity used while displaying the list items (not including custom adapters), defaults to start.
        Can be start, center, or end.
    -->
    <item name="md_items_gravity">start</item>
    
    <!--
        This sets the gravity used while displaying the dialog action buttons, defaults to start.
        
        START (Default)    Neutral     Negative    Positive
        CENTER:            Negative    Neutral     Positive
        END:	           Positive    Negative    Neutral
    -->
    <item name="md_buttons_gravity">start</item>
    
    <!--
        This sets the gravity used while displaying the stacked action buttons, defaults to end.
        Can be start, center, or end.
    -->
    <item name="md_btnstacked_gravity">end</item>

    <!--
        The name of font in assets/fonts used on titles and action buttons
        (null uses device default). E.g. [your-project]/app/main/assets/fonts/[medium]
    -->
    <item name="md_medium_font">Roboto-Medium.ttf</item>

    <!--
        The name of font in assets/fonts used everywhere else, like content and list items
        (null uses device default). E.g. [your-project]/app/main/assets/fonts/[regular]
    -->
    <item name="md_regular_font">Roboto-Regular.ttf</item>

</style>
```

The action button color is also derived from the `android:colorAccent` attribute of the Material theme,
or `colorAccent` attribute of the AppCompat Material theme as seen in the sample project. Manually setting
the color will override that behavior.

---

# Show, Cancel, and Dismiss Callbacks

You can directly setup show/cancel/dismiss listeners from the `Builder` rather than on the resulting
`MaterialDialog` instance.

Also note that the `Builder` has a `cancelable()` method that lets you disable dismissing the dialog
when you tap outside the dialog window.

```java
new MaterialDialog.Builder(this)
    .title("Use Google's Location Services?")
    .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
    .positiveText("Agree")
    .showListener(new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
        }
    })
    .cancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
        }
    })
    .dismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
        }
    })
    .show();
```

---

# Input Dialogs

An input dialog is pretty self explanatory, it retrieves input from the user of your application with
an input field (EditText). You can also display content above the EditText if you desire.

```java
new MaterialDialog.Builder(this)
        .title(R.string.input)
        .content(R.string.input_content)
        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
        .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
            }
        }).show();
```

The input dialog will automatically handle focusing the EditText and displaying the keyboard to allow
the user to immediately enter input. When the dialog is closed, the keyboard will be automatically dismissed.

**Note that the dialog will force the positive action button to be visible, when it's pressed the input
is submitted to the callback.**

**Also Note that the call to `inputType()` is optional.**

## Coloring the EditText

Like action buttons and many other elements of the Material dialog, you can customize the color of a
 input dialog's `EditText`. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 and `widgetColorAttr()` method. Their names and parameter annotations make them self explanatory.
 Note that by default, EditTexts will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.

There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

## Limiting Input Length

The code below will show a little indicator in the input dialog that tells the user how many characters they've
typed. If they type less than 2 characters, or more than 20, the dialog won't allow the input to be submitted.
It will also color the input field and character counter in error color passed for the third parameter.
 
If you pass 0 for the min length, there will be no min length. If you pass -1 for the max length, there will
be no max length. If you don't pass a third parameter at all, it will default to Material red. 

```java
new MaterialDialog.Builder(this)
    .title(R.string.input)
    .inputRangeRes(2, 20, R.color.material_red_500)
    .input(null, null, new MaterialDialog.InputCallback() {
        @Override
        public void onInput(MaterialDialog dialog, CharSequence input) {
            // Do something
        }
    }).show();
```

*Note that `inputRangeRes(int, int, int)` takes a color resource ID for the third parameter, while
`inputRange(int, int, int)` takes a literal color integer for the second parameter. You can use either one, or use
the variation that doesn't take a third parameter at all.

## Custom Invalidation

The easiest way to invalidate (enable or disable the EditText based on whether you think the input is acceptable)
input dialogs is to call `alwaysCallInputCallback()` from the `Builder` so that the callback is invoked
every time the user changes their input. From there, you can constantly check what they've typed. If you
decide they shouldn't be able to submit that, you can disable the submit button using this from within the callback:

```java
dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
```

---

# Progress Dialogs

This library allows you to display progress dialogs with Material design that even use your app's
accent color to color the progress bars (if you use AppCompat to theme your app, or the Material theme on Lollipop).

## Proguard

Normally, `ObjectAnimator` in the context it's used in this library (for custom progress drawables) would
need special proguard rules so that certain elements aren't removed when your app is built in release mode.
Luckily, AAR packages are allowed to specify proguard rules that get included in apps that depend on them.
So you do not need to worry about including any Proguard rules in order to ensure progress bars behave well.

## Indeterminate Progress Dialogs

This will display the classic progress dialog with a spinning circle, see the sample project to see it in action:

```java
new MaterialDialog.Builder(this)
    .title(R.string.progress_dialog)
    .content(R.string.please_wait)
    .progress(true, 0)
    .show();
```

## Determinate (Seek Bar) Progress Dialogs

If a dialog is not indeterminate, it displays a horizontal progress bar that increases up until a max value.
The comments in the code explain what this does.

```java
// Create and show a non-indeterminate dialog with a max value of 150
// If the showMinMax parameter is true, a min/max ratio will be shown to the left of the seek bar.
boolean showMinMax = true;
MaterialDialog dialog = new MaterialDialog.Builder(this)
    .title(R.string.progress_dialog)
    .content(R.string.please_wait)
    .progress(false, 150, showMinMax)
    .show();

// Loop until the dialog's progress value reaches the max (150)
while (dialog.getCurrentProgress() != dialog.getMaxProgress()) {
    // If the progress dialog is cancelled (the user closes it before it's done), break the loop
    if (dialog.isCancelled()) break;
    // Wait 50 milliseconds to simulate doing work that requires progress
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {
        break;
    }
    // Increment the dialog's progress by 1 after sleeping for 50ms
    dialog.incrementProgress(1);
}

// When the loop exits, set the dialog content to a string that equals "Done"
dialog.setContent(getString(R.string.done));
```

See the sample project for this dialog in action, with the addition of threading.

## Make an Indeterminate Dialog Horizontal

By default, indeterminate progress dialogs use a circular progress indicator. From the `Builder`,
you can tell the dialog that it needs to use a horizontal indicator when displaying an indeterminate progress
dialog:

```java
new MaterialDialog.Builder(this)
    .title(R.string.progress_dialog)
    .content(R.string.please_wait)
    .progress(true, 0)
    .progressIndeterminateStyle(true)
    .show();
```

## Coloring the Progress Bar

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 progress dialog's progress bar. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 and `widgetColorAttr()` method. Their names and parameter annotations make them self explanatory.
 Note that by default, progress bars will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.
 
There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

## Custom Number and Progress Formats

Like the stock `ProgressDialog`, you can format the progress min/max numbers and the percentage indicator
of determinate dialogs.

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
    .progress(false, 150, true)
    ...
    .progressNumberFormat("%1d/%2d")
    .progressPercentFormat(NumberFormat.getPercentageInstance())
    ...
    .show();
```

The values passed above are the default.

---

# Tint Helper

You can use the `MDTintHelper` class to dynamically color check boxes, radio buttons, edit texts, and progress bars 
(to get around not being able to change `styles.xml` at runtime). It is used in the library to dynamically color
UI elements to match your set `widgetColor`.

---

# Misc

If you don't want the dialog to automatically be dismissed when an action button is pressed or when
the user selects a list item:

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
        // ... other initialization
        .autoDismiss(false)
        .show();
```

---

# Color Chooser Dialogs

The Builder is used like this:

```java
// Pass a context, along with the title of the dialog
new ColorChooserDialog.Builder(this, R.string.color_palette)
    .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
    .accentMode(accent)  // when true, will display accent palette instead of primary palette
    .doneButton(R.string.md_done_label)  // changes label of the done button
    .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
    .backButton(R.string.md_back_label)  // changes label of the back button
    .preselect(accent ? accentPreselect : primaryPreselect)  // optionally preselects a color
    .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
    .show(this); // an AppCompatActivity which implements ColorCallback
```

The Activity/Fragment you show the dialog in must implement `ColorCallback`:

```java
public class MyActivity implements ColorChooserDialog.ColorCallback {

    // ...

    @Override
    public void onColorSelection(ColorChooserDialog dialog, @ColorInt int color) {
        // TODO
    }
}
```

---

You can also specify custom colors to be displayed if you don't want to use the built-in primary or accent
color palettes (which consist of the entire Material Design Color Palette):

```java
int[] primary = new int[] {
    Color.parseColor("#F44336")
};
int[][] secondary = new int[][] {
    new int[] { Color.parseColor("#EF5350"), Color.parseColor("#F44336"), Color.parseColor("#E53935") }
};

new ColorChooserDialog.Builder(this, R.string.color_palette)
    .titleSub(R.string.colors)
    .customColors(primary, secondary)
    .show(this);
```

The first parameter for primary colors can also take an array resource (`R.array.colors`), which can be
seen in the sample project. If you pass `null` for the second parameter, there will be no sub levels displayed
for top level colors.

## Finding Visible Dialogs

Since the `ColorChooserDialog` is a `DialogFragment`, it attaches to your Activity/Fragment through its `FragmentManager`.
`ColorChooserDialog` has a utility method called `findVisible(AppCompatActivity, String)` that will
find a visible color chooser if any is visible:

```java
ColorChooserDialog primary = ColorChooserDialog.findVisible(getSupportFragmentManager(), ColorChooserDialog.TAG_PRIMARY);

ColorChooserDialog accent = ColorChooserDialog.findVisible(getSupportFragmentManager(), ColorChooserDialog.TAG_ACCENT);

ColorChooserDialog custom = ColorChooserDialog.findVisible(getSupportFragmentManager(), ColorChooserDialog.TAG_CUSTOM);
```

## User Color Input

By default, color chooser dialogs allow the user to input a custom color using RGB sliders or a Hexadecimal input field.
This can be disabled if you don't want users to be able to use it:

```java
new ColorChooserDialog.Builder(this, R.string.color_palette)
    .allowUserColorInput(false)
    .customButton(R.string.md_custom_label)
    .presetsButton(R.string.md_presets_label)
    .show(this);
```

If you want the user to be able to input a custom color, but don't want them to be able to change transparency (alpha):

```java
new ColorChooserDialog.Builder(this, R.string.color_palette)
    .allowUserColorInputAlpha(false)
    .customButton(R.string.md_custom_label)
    .presetsButton(R.string.md_presets_label)
    .show(this);
```

---

# Preference Dialogs

Android's `EditTextPreference`, `ListPreference`, and `MultiSelectListPreference` allow you to associate a preference activity's settings
with user input that's received through typing or selection. Material Dialogs includes `MaterialEditTextPreference`,
`MaterialListPreference`, and `MaterialMultiSelectListPreference` classes that can be used in your preferences XML to automatically use Material-themed
dialogs. See the sample project for details.

By default, all of these preference classes will set their layout to `R.layout.md_preference_custom`. If you 
don't want a default layout to be set, you can provide an attribute on the preferences in your XML:

```
app:useStockLayout="true"
```

---

# File Selector Dialogs

The Builder is used like this:

```java
new FileChooserDialog.Builder(this)
    .initialPath("/sdcard/Download")  // changes initial path, defaults to external storage directory
    .mimeType("image/*") // Optional MIME type filter
    .extensionsFilter(".png", ".jpg") // Optional extension filter, will override mimeType()
    .tag("optional-identifier")
    .goUpLabel("Up") // custom go up label, default label is "..."
    .show(this); // an AppCompatActivity which implements FileCallback
```

The Activity/Fragment you show the dialog in must implement `FileCallback`:

```java
public class MyActivity implements FileChooserDialog.FileCallback {

    // ...

    @Override
    public void onFileSelection(FileChooserDialog dialog, File file) {
        // TODO
        final String tag = dialog.getTag(); // gets tag set from Builder, if you use multiple dialogs
    }
}
```

---

# Folder Selector Dialogs

The Builder is used like this:

```java
// Pass AppCompatActivity which implements FolderCallback
new FolderChooserDialog.Builder(this)
    .chooseButton(R.string.md_choose_label)  // changes label of the choose button
    .initialPath("/sdcard/Download")  // changes initial path, defaults to external storage directory
    .tag("optional-identifier")
    .goUpLabel("Up") // custom go up label, default label is "..."
    .show(this);
```

The Activity/Fragment you show the dialog in must implement `FolderCallback`:

```java
public class MyActivity implements FolderChooserDialog.FolderCallback {

    // ...

    @Override
    public void onFolderSelection(FolderChooserDialog dialog, File folder) {
        // TODO
        final String tag = dialog.getTag(); // gets tag set from Builder, if you use multiple dialogs
    }
}
```

---

Optionally, you can allow users to have the ability to create new folders from this dialog:

```java
new FolderChooserDialog.Builder(this)
    .chooseButton(R.string.md_choose_label)  // changes label of the choose button
    .initialPath("/sdcard/Download")  // changes initial path, defaults to external storage directory
    .tag("optional-identifier")
    .allowNewFolder(true, R.string.new_folder)  // pass 0 in the second parameter to use default button label
    .show(this);
```

---

# Simple List Dialogs

Simple List Dialogs are a specific style of list dialogs taken from the Material Design Guidelines: https://www.google.com/design/spec/components/dialogs.html#dialogs-simple-dialogs

This library's implementation is just a pre-made adapter that you can pass to the `MaterialDialog.Builder`.

```java
final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
    @Override
    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
        // TODO
    }
});

adapter.add(new MaterialSimpleListItem.Builder(this)
    .content("username@gmail.com")
    .icon(R.drawable.ic_account_circle)
    .backgroundColor(Color.WHITE)
    .build());
adapter.add(new MaterialSimpleListItem.Builder(this)
    .content("user02@gmail.com")
    .icon(R.drawable.ic_account_circle)
    .backgroundColor(Color.WHITE)
    .build());
adapter.add(new MaterialSimpleListItem.Builder(this)
    .content(R.string.add_account)
    .icon(R.drawable.ic_content_add)
    .iconPaddingDp(8)
    .build());

new MaterialDialog.Builder(this)
    .title(R.string.set_backup)
    .adapter(adapter, null)
    .show();
```

---
