# Material Dialogs

The code you see below is also found in the sample project. You can download a APK of the sample here: https://github.com/afollestad/material-dialogs/blob/master/sample/sample.apk. The sample's also available on Google Play: https://play.google.com/store/apps/details?id=com.afollestad.materialdialogssample.

---

### Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Groovy
dependencies {
    compile 'com.afollestad:material-dialogs:0.6.0'
}
```

[ ![Download](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs/_latestVersion)

***Make sure*** you're using the jCenter repository, Android Studio uses this repository by default.

Check back here frequently for version updates.

---

### What's New

For the full history, see the [Changelog](https://github.com/afollestad/material-dialogs/blob/master/CHANGELOG.md).


###### Version 0.6.0

> 1. Another pull request from [Kevin Barry](https://github.com/teslacoil) that fixes the "vibrating window" effect.
> 2. The ability to enable the single choice callback to be called everytime a checkbox is checked, rather than when the positive action is pressed; this matches up with the multi-choice variation. Thanks [hzsweers](https://github.com/hzsweers): https://github.com/afollestad/material-dialogs/pull/170
> 3. The ability to override the selectors for action buttons and list items, through `Builder` methods (e.g. `selector` and `btnSelector`, along with the res variants) and [global theming](https://github.com/afollestad/material-dialogs#global-theming).
> 4. An exception is now thrown if you attempt to show a dialog on a non-UI thread, which will help those who accidentally do so avoid issues.

###### Version 0.5.8 – 0.5.9

> 1. Pull request from [hzsweers](https://github.com/hzsweers): https://github.com/afollestad/material-dialogs/pull/167
>
> IntDefs cannot safely be used in AARs, so we're back to using a enum (`GravityEnum`) for the various gravity methods.
> The RecyclerView dependency is also now provided, so it's not a required dependency but it's supported.
>
> 2. Pull request from [Kevin Barry](https://github.com/teslacoil) that makes some very important, mostly visual, improvements/fixes: https://github.com/afollestad/material-dialogs/pull/169

###### Version 0.5.5 – 0.5.7

> 1. Added `itemColor` and `itemColorRes` methods to the Builder for changing default list item color.
> 2. Added `accentColor` and `accentColorRes` methods as a convenience method to the three methods `positiveColor`, `negativeColor`, and `neutralColor` (and their 'res' variants).
> 3. Added `ThemeSingleton`, for internal use only right now unless you really think your app needs it. Used for dynamic global theming (changing at/after runtime).
> 4. In the Builder, the `icon` method for a drawable resource ID was renamed to `iconRes` for consistency.
>
> Quick fix in 0.5.6, fixed title gravity in 0.5.7.

###### Version 0.5.4

> 1. Fixes for positioning of negative button (bug that came up with the previous update that improved RTL support).
> 2. Fix for multichoice dialogs not keeping checked states correctly if no preselection is used.
> 3. New color chooser dialog in the sample project, feel free to use it in your apps!
> 4. Support for detection if a RecyclerView custom view is scrollable. Note that the stock RecyclerView LayoutManagers do not support using wrap_content as height correctly, so RecyclerViews in dialogs will take up the max dialog height.
> 5. Other small fixes.
>
> Thanks to those who help me out with pull requests, also!

###### Version 0.5.3

> 1. Global theming attributes for dialog background color and divider color. See the [Global Theming section](https://github.com/afollestad/material-dialogs#global-theming).
> 2. These attributes can be set through the `Builder` too (`dividerColor`, `dividerColorRes`, `backgroundColor`, `backgroundColorRes`).
> 2. Lots and lots of improvements for RTL support! This includes the title, list content, and action buttons in RTL layout mode (API 17 and above only).
>       One thing to come from this is the action buttons are no longer actual `Button` instances, they're text views wrapped in frame layouts in order to make gravity work correctly.
> 3. Other bug fixes.

###### Version 0.5.1 - 0.5.2

> Lots of fixes from [hzsweers](https://github.com/hzsweers)'s pull request! https://github.com/afollestad/material-dialogs/pull/149
>
> 1. Support for setting key listener in the `Builder`.
> 2. More RTL layout improvements, use of `Alignment` enum replaced with the regular Gravity constants.
> 3. Updates to `MaterialDialogCompat`.
> 4. The ability to invoke the multi choice callback every time a checkbox is checked/unchecked, rather than waiting until the positive action button is pressed (if it's there). `alwaysCallMultiChoiceCallback()` method added to the `Builder`.
> 5. Other various improvements, see the pull request from the link above for details.

###### Version 0.5.0

> 1. The ability to choose whether or not custom views are placed inside of a `ScrollView` (the second parameter of `customView()` in the `Builder`). This is heavily based off a pull request by [Kevin Barry](https://github.com/teslacoil), thanks for your help! See the [Custom Views](https://github.com/afollestad/material-dialogs#custom-views) section for more details, see the sample project for an example in action.
> 2. An enormous amount of fixes for padding and spacing throughout the different types of dialogs. A top divider is also used when there's scrollable content.
> 3. Other bug fixes and improvements throughout.

---

### Basic Dialog

Here's a basic example that mimics the dialog you see on Google's Material design guidelines
(here: http://www.google.com/design/spec/components/dialogs.html#dialogs-usage). Note that you can
always substitute literal strings and string resources for methods that take strings, the same goes
for color resources (e.g. `titleColor` and `titleColorRes`).

```java
new MaterialDialog.Builder(this)
        .title("Use Google's Location Services?")
        .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
        .positiveText("Agree")
        .negativeText("Disagree")
        .show();
```

On Lollipop (API 21) or if you use AppCompat, the Material dialog will automatically match the `positiveColor`
(which is used on the positive action button) to the `colorAccent` attribute of your styles.xml theme.

If the content is long enough, it will become scrollable and a divider will be displayde above the action buttons.

---

### Migration from AlertDialogs

If you're migrating old dialogs you could use ```MaterialDialogCompat```. You need change imports and replace ```AlertDialog.Builder``` with ```MaterialDialogCompat.Builder```:

```java
MaterialDialogCompat.Builder dialogBuilder = new MaterialDialogCompat.Builder(context);
dialogBuilder.setMessage(messageId);
dialogBuilder.setTitle(titleId);
dialogBuilder.setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }
});
dialogBuilder.create().show();
```

But it's highly recommended to use original ```MaterialDialog``` API for new usages.

---

### Displaying an Icon

MaterialDialog supports the display of an icon just like the stock AlertDialog; it will go to the left of the title.

```java
Drawable d = // ... get from somewhere...
new MaterialDialog.Builder(this)
        .title("Use Google's Location Services?")
        .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
        .positiveText("Agree")
        .icon(d)
        .show();
```

You can substitute a `Drawable` instance for a drawable resource ID or attribute ID, which is recommended.

---

### Stacked Action Buttons

If you have multiple action buttons that together are too wide to fit on one line, the dialog will stack the
buttons to be vertically orientated.

```java
new MaterialDialog.Builder(this)
        .title("Use Google's Location Services?")
        .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
        .positiveText("Turn on speed boost right now!")
        .negativeText("No thanks")
        .show();
```

You can also force the dialog to stack its buttons with the `forceStacking()` method of the `Builder`.

---

### Neutral Action Button

You can specify neutral text in addition to the positive and negative text. It will show the neutral
action on the far left.

```java
new MaterialDialog.Builder(this)
        .title("Use Google's Location Services?")
        .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
        .positiveText("Agree")
        .negativeText("Disagree")
        .neutralText("More info")
        .show();
```

---

### Callbacks

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

### List Dialogs

Creating a list dialog only requires passing in an array of strings. The callback (`itemsCallback`) is
also very simple.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new CharSequence[]{"Twitter", "Google+", "Instagram", "Facebook"})
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

### Single Choice List Dialogs

Single choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackSingleChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display radio buttons next to list items.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new CharSequence[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
            }
        })
        .positiveText("Choose")
        .show();
```

If you want to preselect an item, pass an index 0 or greater in place of -1 in `itemsCallbackSingleChoice()`.
If `autoDismiss` is turned off, then you must manually dismiss the dialog in the callback. Auto dismiss is on by default.
When `positiveText()` is not used, the callback will be called every time you select an item since no action is
available to press, without the dialog being dismissed. You can pass `positiveText()` or the other action
buttons to the builder to force it to display the action buttons below your list, however this is only
useful in some specific cases.

---

### Multi Choice List Dialogs

Multiple choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackMultiChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display check boxes next to list items, and the callback can return multiple selections.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new CharSequence[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMulti() {
            @Override
            public void onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
            }
        })
        .positiveText("Choose")
        .show();
```

If you want to preselect item(s), pass an array of indices in place of null in `itemsCallbackSingleChoice()`.
For an example, `new Integer[] { 2, 5 }`. If `autoDismiss` is turned off, then you must manually
dismiss the dialog in the callback. Auto dismiss is on by default. When action buttons are not added, the
callback will be called every time you select an item since no action is available to press, without the
dialog being dismissed. You can pass `positiveText()` or the other action buttons to the builder to force
it to display the action buttons below your list, however this is only useful in some specific cases.

---

### Custom List Dialogs

Like Android's native dialogs, you can also pass in your own adapter via `.adapter()` to customize
exactly how you want your list to work. You also have access to the dialog's list via `getListView()` method.

```java
MaterialDialog dialog = new MaterialDialog.Builder(this)
        .title(R.string.socialNetworks)
        .adapter(new ButtonItemAdapter(this, R.array.socialNetworks))
        .build();

ListView listView = dialog.getListView();
if (listView != null) {
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, "Clicked item " + position, Toast.LENGTH_SHORT).show();
        }
    });
}

dialog.show();
```

---

### Custom Views

Custom views are very easy to implement.

```java
boolean wrapInScrollView = true;
new MaterialDialog.Builder(this)
        .title("Google Wifi")
        .customView(R.layout.custom_view, wrapInScrollView)
        .positiveText("Connect")
        .positiveColor(Color.parseColor("#03a9f4"))
        .build()
        .show();
```

If `wrapInScrollView` is true, then the library will place your custom view inside of a ScrollView for you.
This allows users to scroll your custom view if necessary (small screens, long content, etc.). However, there are cases
when you don't want that behavior. This mostly consists of cases when you'd have a ScrollView in your custom layout,
including ListViews, RecyclerViews, WebViews, GridViews, etc. The sample project contains examples of using both true
 and false for this parameter.

Your custom view will automatically have padding put around it when `wrapInScrollView` is true. Otherwise
you're responsible for using padding values that look good with your content.

---

### Theming

Before Lollipop, theming AlertDialogs was basically impossible without using reflection and custom drawables.
Since KitKat, Android became more color neutral but AlertDialogs continued to use Holo Blue for the title and
title divider. Lollipop has improved even more, with no colors in the dialog by default other than the action
buttons. This library makes theming even easier. Here's a basic example:

```java
final int materialRed500 = Color.parseColor("#D50000");
new MaterialDialog.Builder(this)
        .title("Use Google's Location Services?")
        .content("Let Google help apps determine location. This means sending anonymous location data to Google, even when no apps are running.")
        .positiveText("Agree")
        .negativeText("Disagree")
        .positiveColor(materialRed500)
        .negativeColor(materialRed500)
        .neutralColor(materialRed500)
        .titleGravity(Gravity.CENTER_HORIZONTAL)
        .titleColor(materialRed500)
        .contentColor(Color.WHITE)
        .dividerColorRes(R.color.material_pink_500)
        .backgroundColorRes(R.color.material_blue_grey_800)
        .theme(Theme.DARK)
        .show();
```

To see more colors that fit the Material design palette, see this page: http://www.google.com/design/spec/style/color.html#color-color-palette

---

### Global Theming

By default, the dialog inherits and extracts theme colors from other attributes and theme colors of the app
or operating system. This behavior can be overridden in your Activity themes:

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">

    <!--
        All dialogs will default to Theme.DARK with this set to true.
    -->
    <item name="md_dark_theme">true</item>

    <!--
        Applies an icon to all dialogs.
    -->
    <item name="md_dark_theme">true</item>

    <!--
        By default, the title text is black or white based on the theme.
    -->
    <item name="md_title_color">#E91E63</item>

    <!--
        By default, the content text is derived from the
        ?android:textColorSecondary OS attribute.
    -->
    <item name="md_content_color">#9C27B0</item>

    <!--
        By default, the accent color is derived from the colorAccent attribute of
        AppCompat or android:colorAccent attribute of the Material theme.
    -->
    <item name="md_accent_color">#673AB7</item>

    <!--
        By default, the list item text color is black for the light
        theme and white for the dark theme.
    -->
    <item name="md_item_color">#9C27B0</item>

    <!--
        This overrides the default dark or light dialog background color.
        Note that if you use a dark color here, you should set md_dark_theme to
        true so text and selectors look visible
    -->
    <item name="md_background_color">#37474F</item>

    <!--
        This overrides the color used for the top and bottom dividers used when
        content is scrollable
    -->
    <item name="md_divider_color">#E91E63</item>

    <!--
        This overrides the selector used on list items and stacked action buttons
    -->
    <item name="md_selector">@drawable/selector</item>

    <!--
        This overrides the selector used on action buttons
    -->
    <item name="md_btn_selector">@drawable/selector</item>

</style>
```

The action button color is also derived from the `android:colorAccent` attribute of the Material theme,
or `colorAccent` attribute of the AppCompat Material theme as seen in the sample project. Manually setting
the color will override that behavior.

---

### Show, Cancel, and Dismiss Callbacks

You can directly setup show/cancel/dismiss listeners from the `Builder` rather than on the resulting
`MaterialDialog` instance:

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

### Misc

If you need to access a View in the custom view set to a MaterialDialog, you can use `getCustomView()` of
MaterialDialog. This is especially useful if you pass a layout resource to the Builder.

```java
MaterialDialog dialog = //... initialization via the builder ...
View view = dialog.getCustomView();
```

If you want to get a reference to the title frame (which contains the icon and title, e.g. to change visibility):

```java
MaterialDialog dialog = //... initialization via the builder ...
TextView title = dialog.getTitleFrame();
```

If you want to get a reference to one of the dialog action buttons (e.g. to enable or disable buttons):

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

If you don't want the dialog to automatically be dismissed when an action button is pressed or when
the user selects a list item:

```java
MaterialDialog dialog new MaterialDialog.Builder(this)
        // ... other initialization
        .autoDismiss(false)
        .show();
```

To customize fonts:

```java
Typeface titleAndActions = // ... initialize
Typeface contentAndListItems = // ... initialize
MaterialDialog dialog new MaterialDialog.Builder(this)
        // ... other initialization
        .typeface(titleAndActions, contentAndListItems)
        .show();
```
