# Material Dialogs

![Screenshots](https://raw.githubusercontent.com/afollestad/material-dialogs/master/art/mdshowcase.png)

# Sample Project

You can download the latest sample APK from this repo here: https://github.com/afollestad/material-dialogs/blob/master/sample/sample.apk

It's also on Google Play:

<a href="https://play.google.com/store/apps/details?id=com.afollestad.materialdialogssample">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

Having the sample project installed is a good way to be notified of new releases. Although Watching this 
repository will allow GitHub to email you whenever I publish a release.

---

# Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Gradle
dependencies {
    compile 'com.afollestad:material-dialogs:0.7.3.1'
}
```

[ ![Download](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs/_latestVersion)

### If jCenter is Having Issues (the library can't be resolved)

Add this to your app's build.gradle file:

```Gradle
repositories {
    maven { url 'https://dl.bintray.com/drummer-aidan/maven' }
}
```

This will reference Bintray's Maven repository that contains material-dialogs directly, rather
than going through jCenter first.

---

# What's New

See the project's Releases page for a list of versions with their changelogs.

### [View Releases](https://github.com/afollestad/material-dialogs/releases)

If you Watch this repository, GitHub will send you an email every time I publish an update.

---

# Basic Dialog

First of all, note that `MaterialDialog` extends `DialogBase`, which extends `AlertDialog`. While
a very small number of the stock methods are purposely deprecated and don't work, you have access
to methods such as `dismiss()`, `setTitle()`, `setIcon()`, `setCancelable()`, etc. Alternatives are discussed below.

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

On Lollipop (API 21+) or if you use AppCompat, the Material dialog will automatically match the `positiveColor`
(which is used on the positive action button) to the `colorAccent` attribute of your styles.xml theme.

If the content is long enough, it will become scrollable and a divider will be displayed above the action buttons.

---

# Migration from AlertDialogs

If you're migrating old dialogs you could use ```AlertDialogWrapper```. You need change imports and replace ```AlertDialog.Builder``` with ```AlertDialogWrapper.Builder```:

```java
new AlertDialogWrapper.Builder(this)
        .setTitle(R.string.title)
        .setMessage(R.string.message)
        .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
```

But it's highly recommended to use original ```MaterialDialog``` API for new usages.

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

You can also force the dialog to stack its buttons with the `forceStacking()` method of the `Builder`.

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

To know when the user selects an action button, you set a callback. To do this, use the `ButtonCallback`
class and override its `onPositive()`, `onNegative()`, or `onNeutral()` methods as needed. The advantage
to this is that you can override button functionality *À la carte*, so no need to stub empty methods.

```java
new MaterialDialog.Builder(this)
        .callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
            }
        });

new MaterialDialog.Builder(this)
        .callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
            }
        });

new MaterialDialog.Builder(this)
        .callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
            }
        });
```

If `autoDismiss` is turned off, then you must manually dismiss the dialog in these callbacks. Auto dismiss is on by default.

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
every time the user selects an item.

## Coloring Radio Buttons

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 dialog's radio buttons. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 and `widgetColorAttr()` method. Their names and parameter annotations make them self explanatory.
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
                 * returning false here won't allow the newly selected check box to actually be selected.
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
every time the user selects an item.

## Coloring Check Boxes

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 dialog's check boxes. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 and `widgetColorAttr()` method. Their names and parameter annotations make them self explanatory. 
 Note that by default, check boxes will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.
 
There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

---

# Custom List Dialogs

Like Android's native dialogs, you can also pass in your own adapter via `.adapter()` to customize
exactly how you want your list to work.

```java
new MaterialDialog.Builder(this)
        .title(R.string.socialNetworks)
        .adapter(new ButtonItemAdapter(this, R.array.socialNetworks),
                new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        Toast.makeText(MainActivity.this, "Clicked item " + which, Toast.LENGTH_SHORT).show();
                    }
                })
        .show();
```

If you need access to the `ListView`, you can use the `MaterialDialog` instance:

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
        ...
        .build();

ListView list = dialog.getListView();
// Do something with it

dialog.show();
```

Note that you don't need to be using a custom adapter in order to access the `ListView`, it's there for single/multi choice dialogs, regular list dialogs, etc.

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
        .dividerColorRes(R.color.material_pink_500)
        .backgroundColorRes(R.color.material_blue_grey_800)
        .positiveColorRes(R.color.material_red_500)
        .neutralColorRes(R.color.material_red_500)
        .negativeColorRes(R.color.material_red_500)
        .widgetColorRes(R.color.material_red_500)
        .show();
```

The names are self explanatory for the most part. The `widgetColor` method, discussed in a few other
sections of this tutorial, applies to progress bars, check boxes, and radio buttons. Also note that 
each of these methods have 3 variations for setting a color directly, using color resources, and using 
color attributes.

## Selectors

Theming selectors allows you to change colors for pressable things:

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

***An important note related to using custom action button selectors***: make sure your selector drawable references
inset drawables like the default ones do - this is important for correct action button padding.

## Gravity

It's probably unlikely you'd want to change gravity of elements in a dialog, but it's possible.

```java
new MaterialDialog.Builder(this)
        .titleGravity(GravityEnum.CENTER_HORIZONTAL)
        .contentGravity(GravityEnum.CENTER_HORIZONTAL)
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
    <item name="md_regular_font">Roboto-Medium.ttf</item>

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
typed, and how many more they can type before reaching a certain limit. If they go over that limit,
the dialog won't allow them to submit the input. It will also color the input field and length indicator
with an error color of your choosing (or the default if you don't specify one).

```java
new MaterialDialog.Builder(this)
        .title(R.string.input)
        .inputMaxLengthRes(20, R.color.material_red_500)
        .input(null, null, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
            }
        }).show();
```

*Note that `inputMaxLengthRes(int, int)` takes a color resource ID for the second parameter, while
`inputMaxLength(int, int)` takes a literal color integer for the second parameter. You can use either one.
If you want to use the default error color from the guidelines, you can use `inputMaxLength(int)` which doesn't
take the second error color parameter*

---

# Progress Dialogs

This library allows you to display progress dialogs with Material design that even use your app's
accent color to color the progress bars (if you use AppCompat to theme your app, or the Material theme on Lollipop).

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

## Coloring the Progress Bar

Like action buttons and many other elements of the Material dialog, you can customize the color of a 
 progress dialog's progress bar. The `Builder` class contains a `widgetColor()`, `widgetColorRes()`,
 and `widgetColorAttr()` method. Their names and parameter annotations make them self explanatory.
 Note that by default, progress bars will be colored with the color held in `colorAccent` (for AppCompat)
 or `android:colorAccent` (for the Material theme) in your Activity's theme.
 
There's also a global theming attribute as shown in the Global Theming section of this README: `md_widget_color`.

---

# Preference Dialogs

Android's `EditTextPreference`, `ListPreference`, and `MultiSelectListPreference` allow you to associate a preference activity's settings
with user input that's received through typing or selection. Material Dialogs includes `MaterialEditTextPreference`,
`MaterialListPreference`, and `MaterialMultiSelectListPreference` classes that can be used in your preferences XML to automatically use Material-themed
dialogs. See the sample project for details.

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
MaterialDialog dialog new MaterialDialog.Builder(this)
        // ... other initialization
        .autoDismiss(false)
        .show();
```
