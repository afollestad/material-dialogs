# Material Dialogs

Welcome. This library was designed to solve a personal problem with my apps, I use AppCompat to use
Material theming on versions of Android below Lollipop. However, AppCompat doesn't theme AlertDialogs
to use Material on pre-Lollipop. This library allows you to use a consistently Material themed dialog on
all versions of Android, along with specific customizations that make it easier to brand the dialog.

Here's a basic example that mimics the dialog you see on Google's Material design guidelines
(here: http://www.google.com/design/spec/components/dialogs.html#dialogs-usage). Note that you can
always substitute literal strings and string resources for methods that take strings, the same goes
for color resources (e.g. `titleColor` and `titleColorRes`).

```java
new MaterialDialog.Builder(this)
        .title("Permissions")
        .content("This app determines your phone's location and shares it with Google in order to serve personalized alerts to you. This allows for a better overall app experience.")
        .theme(Theme.LIGHT)  // the default is light, so you don't need this line
        .positiveText(R.string.accept)  // the default is 'Accept'
        .negativeText(R.string.decline)  // leaving this line out will remove the negative button
        .build()
        .show();
```

![Example 1](/art/example1.png)

On Lollipop (API 21), the Material dialog will automatically match the `positiveColor` (which is used on the
positive action button) to the `colorAccent` attribute of your styles.xml theme.

---

### Stacked Buttons

If the action text is too long, it will stack the buttons as also seen on Google's Material design guidelines.

```java
new MaterialDialog.Builder(this)
        .title("Permissions")
        .content("This app determines your phone's location and shares it with Google in order to serve personalized alerts to you. This allows for a better overall app experience.")
        .positiveText("Turn on speed boost")
        .negativeText("No thanks")
        .build()
        .show();
```

![Example 2](/art/example2.png)

---

### Neutral Button

You can specify neutral text in addition to the positive and negative text. On smaller screens, it will
cause the buttons to be stacked due to limited space. On a larger screen, however, it will show the neutral
action on the far left as seen on the Design Guidelines (here: http://www.google.com/design/spec/components/dialogs.html#dialogs-actions).

```java
new MaterialDialog.Builder(this)
        .title("Permissions")
        .content("This app determines your phone's location and shares it with Google in order to serve personalized alerts to you. This allows for a better overall app experience.")
        .positiveText("Accept")
        .negativeText("Decline")
        .neutralText("More info")
        .build()
        .show();
```

The result on a tablet:

![Example 3](/art/example3.png)

---

### Callbacks

To know when the user selects a button, you set a callback. There's three variations of the callback for the action buttons:

```java
new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive() {
                    }
                });

new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive() {
                    }

                    @Override
                    public void onNegative() {
                    }
                });

new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.FullCallback() {
                    @Override
                    public void onPositive() {
                    }

                    @Override
                    public void onNegative() {
                    }

                    @Override
                    public void onNeutral() {
                    }
                });
```

You can choose which one to use based on which actions you make visible, and which actions need to trigger an event.
If you pass text to an action, it will become visible (not including the positive action which is always visible
and will default to 'Accept' unless you make the dialog a list dialog).
You don't need a callback to make actions visible. But the dialog will not dismiss when an action is pressed if no callback is set for it.

---

### List Dialogs

Creating a list dialog only requires passing in an array of strings. The callback (`itemsCallback`) is
also very simple.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new String[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(int which, String text) {
            }
        })
        .build()
        .show();
```

![Example 4](/art/example4.png)

---

### Single Choice List Dialogs

Single choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackSingleChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display radio buttons next to list items.

This also makes it so that an action button has to be pressed, tapping a list item won't dismiss the dialog.
Note that this means the positive action button callback will be overridden if you specify one.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new String[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallbackSingleChoice(new MaterialDialog.ListCallbackMulti() {
            @Override
            public void onSelection(int which[], String[] text) {
            }
        })
        .positiveText("Choose")
        .build()
        .show();
```

The result:

![Example 5](/art/example5.png)

---

### Multi Choice List Dialogs

Multiple choice list dialogs are almost identical to regular list dialogs. The only difference is that
you use `itemsCallbackMultiChoice` to set a callback rather than `itemsCallback`. That signals the dialog to
display check boxes next to list items, and the callback can return multiple selections.

This also makes it so that an action button has to be pressed, tapping a list item won't dismiss the dialog.
Note that this means the positive action button callback will be overridden if you specify one.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new String[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallbackMultiChoice(new MaterialDialog.ListCallbackMulti() {
            @Override
            public void onSelection(Integer[] which, String[] text) {
            }
        })
        .positiveText("Choose")
        .build()
        .show();
```

The result:

![Example 6](/art/example6.png)

---

### Custom Views

Custom views are very easy to implement. To match the dialog show here: http://www.google.com/design/spec/components/dialogs.html#dialogs-behavior

```java
new MaterialDialog.Builder(this)
        .title("Google Wifi")
        .positiveText(R.string.accept)
        .customView(R.layout.custom_view)
        .positiveText("Connect")
        .positiveColor(Color.parseColor("#03a9f4"))
        .build()
        .show();
```

Where `custom_view.xml` contains a LinearLayout of TextViews, an EditText, and a CheckBox. No padding
is used on the top, bottom, left, or right of the root view, that's all stock to the dialog.

`MaterialDialog` inserts your view into a `ScrollView` and displays a divider above the action buttons,
so don't wrap your custom view in a scroll view and don't worry about it being too long or needing a divider.
However, you should avoid making any content that wouldn't belong in a dialog because of its size.

![Example 7](/art/example7.png)

---

### Theming

Before Lollipop, theming AlertDialogs was basically impossible without using reflection and custom drawables.
Since KitKat, Android became more color neutral but AlertDialogs continued to use Holo Blue for the title and
title divider. Lollipop has improved even more, with no colors in the dialog by default other than the action
buttons. This library makes theming even easier. Here's a basic example:

```java
final int materialRed500 = Color.parseColor("#D50000");
new MaterialDialog.Builder(this)
        .title("Permissions")
        .content("This app determines your phone's location and shares it with Google in order to serve personalized alerts to you. This allows for a better overall app experience.")
        .positiveText("Accept")
        .negativeText("Decline")
        .positiveColor(materialRed500)
        .titleAlignment(Alignment.CENTER)
        .titleColor(materialRed500)
        .theme(Theme.DARK)
        .build()
        .show();
```

The result:

![Example 8](/art/example8.png)

To see more colors that fit the Material design palette, see this page: http://www.google.com/design/spec/style/color.html#color-color-palette

---

### Maven/Gradle Dependency

Coming soon