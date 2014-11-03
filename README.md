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

The result is this:

![Example 1 screenshot](/art/example1.png)