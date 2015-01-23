# Material Dialogs

![Screenshots](https://github.com/afollestad/material-dialogs/blob/master/art/md_screenshots.jpg)

### Sample Project

You can download the latest sample APK from this repo here: https://github.com/afollestad/material-dialogs/blob/master/sample/sample.apk

It's also on Google Play:

<a href="https://play.google.com/store/apps/details?id=com.afollestad.materialdialogssample">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

Having the sample project installed is a good way to be notified of new releases.

---

### Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Gradle
dependencies {
    compile 'com.afollestad:material-dialogs:0.6.1.5'
}
```

[ ![Download](https://api.bintray.com/packages/drummer-aidan/maven/material-dialogs/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-dialogs/_latestVersion)

---

### What's New

See the project's Releases page for a list of versions with their changelogs.

[ ![View Releases](http://dabuttonfactory.com/b.png?t=View%20Releases&f=sans-serif-Bold&ts=14&tc=ffffff&c=5&bgt=unicolored&bgc=1466A7&hp=20&vp=11) ](https://github.com/afollestad/material-dialogs/releases)

---

### Basic Dialog

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

On Lollipop (API 21) or if you use AppCompat, the Material dialog will automatically match the `positiveColor`
(which is used on the positive action button) to the `colorAccent` attribute of your styles.xml theme.

If the content is long enough, it will become scrollable and a divider will be displayed above the action buttons.

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
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.agree)
        .icon(R.drawable.icon)
        .show();
```

---

### Stacked Action Buttons

If you have multiple action buttons that together are too wide to fit on one line, the dialog will stack the
buttons to be vertically orientated.

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

### Neutral Action Button

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

### Single Choice List Dialogs

Single choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackSingleChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display radio buttons next to list items.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .items(R.array.items)
        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
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

---

### Multi Choice List Dialogs

Multiple choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackMultiChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display check boxes next to list items, and the callback can return multiple selections.

```java
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .items(R.array.items)
        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMulti() {
            @Override
            public void onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
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
        .title(R.string.title)
        .customView(R.layout.custom_view, wrapInScrollView)
        .positiveText(R.string.positive)
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
new MaterialDialog.Builder(this)
        .title(R.string.title)
        .content(R.string.content)
        .positiveText(R.string.positive)
        .neutralText(R.string.neutral)
        .negativeText(R.string.negative)
        .positiveColorRes(R.color.material_red_500)
        .neutralColorRes(R.color.material_red_500)
        .negativeColorRes(R.color.material_red_500)
        .neutralColorRes(R.color.material_red_500)
        .titleGravity(GravityEnum.CENTER_HORIZONTAL)
        .contentGravity(GravityEnum.CENTER_HORIZONTAL)
        .titleColorRes(R.color.material_red_500)
        .contentColorRes(Color.WHITE)
        .dividerColorRes(R.color.material_pink_500)
        .backgroundColorRes(R.color.material_blue_grey_800)
        .btnSelectorStacked(R.drawable.custom_btn_selector_stacked)
        .btnSelector(R.drawable.custom_btn_selector)
        .btnSelector(R.drawable.custom_btn_selector_primary, DialogAction.POSITIVE)
        .listSelector(R.drawable.custom_list_and_stackedbtn_selector)
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
        By default, the positive action text color is derived
        from the colorAccent attribute of AppCompat or android:colorAccent
        attribute of the Material theme.
    -->
    <item name="md_neutral_color">#673AB7</item>

    <!--
        By default, the positive action text color is derived
        from the colorAccent attribute of AppCompat or android:colorAccent
        attribute of the Material theme.
    -->
    <item name="md_negative_color">#673AB7</item>

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