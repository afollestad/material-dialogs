# Material Dialogs

Welcome. This library was designed to solve a personal problem with my apps, I use AppCompat to use
Material theming on versions of Android below Lollipop. However, AppCompat doesn't theme AlertDialogs
to use Material on pre-Lollipop. This library allows you to use a consistently Material themed dialog on
all versions of Android, along with specific customizations that make it easier to brand the dialog.

Here's a basic example that mimics the dialog you see on Google's Material design guidelines
(here: http://www.google.com/design/spec/components/dialogs.html#dialogs-usage):

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

---

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
If you pass text to an action, it will become visible; you don't need a callback to do that. But the dialog will
not dismiss when an action is pressed if no callback is set for it.

---

Creating a list dialog only requires passing in an array of strings. The callback (`itemsCallback`) is
also very simple.

```java
new MaterialDialog.Builder(this)
        .title("Social Networks")
        .items(new String[]{"Twitter", "Google+", "Instagram", "Facebook"})
        .itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(int which, String text) {
                Toast.makeText(MyActivity.this, which + ": " + text, Toast.LENGTH_LONG).show();
            }
        })
        .build()
        .show();
```

![Example 3](/art/example3.png)

---

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

![Example 4](/art/example4.png)