2.7.0

1. Undid dialog max width changes again, to what they were before they looked small. Added `maxWidth` setter function that you can use to custom dialog max widths if you wish, although it's discouraged.
2. Added a `dialogWrapContent` parameter to `customView(...)` which instructs the dialog to set its max width to the measured width of your custom view.
3. The `datetime` dialogs use the `dialogWrapContent` parameter above, which fixes how they look in landscape etc.  
4. Misc. bug fixes and code cleanup.