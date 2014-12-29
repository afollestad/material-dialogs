Changelog
=========

###### Version 0.5.0

> 1. The ability to choose whether or not custom views are placed inside of a `ScrollView` (the second parameter of `customView()` in the `Builder`). This is heavily based off a pull request by [Kevin Barry](https://github.com/teslacoil), thanks for your help! See the [Custom Views](https://github.com/afollestad/material-dialogs#custom-views) section for more details, see the sample project for an example in action.
> 2. An enormous amount of fixes for padding and spacing throughout the different types of dialogs. A top divider is also used when there's scrollable content.
> 3. Other bug fixes and improvements throughout.

###### Version 0.4.8 – 0.4.9

> 1. Improvements for padding in list dialogs.
> 2. Fixed the `forceStacking` option.
> 3. Single choice dialogs will wait to send selection callbacks until positive action button is pressed, if the positive action button is set.
> 4. Pull request from [hzsweers](https://github.com/hzsweers): https://github.com/afollestad/material-dialogs/pull/146
> 5. List items use pure black or white text depending on theme by default, rather than the former gray-ish color.

###### Version 0.4.6 – 0.4.7

> 1. Yet more fixes thanks to a pull request from [hzsweers](https://github.com/hzsweers).
> 2. Note that the 3 variations of the action callbacks are deprecated and replaced with the single `ButtonCallback` interface.
> 3. A fix for action button text styling on Lollipop, thanks [plusCubed](https://github.com/plusCubed)!
> 4. Other fixes and improvements.
> 5. The ability to force the action buttons to be stacked (see the [Misc.](#misc) section).

###### Version 0.4.4 – 0.4.5

> 1. Crash fix for Huawei devices
> 2. Removed some unnecessary logging.
> 3. New methods in `MaterialDialogCompat.Builder`
> 4. Other crash fixes and improvements.
> 5. Memory management improvements for Typefaces (thanks [Kevin Barry](https://github.com/teslacoil) of Nova Launcher!)
> 6. Added `dismiss`, `cancel`, and `show` listener methods to the `Builder`.

###### Version 0.4.1 – 0.4.3

> 1. Added `md_item_color` attribute to global theming.
> 2. Added `md_icon` attribute to global theming.
> 3. Fixed a crash bug on pre-Lollipop devices related to list dialogs.
> 4. Fixed list item default color on dark dialogs for pre-Lollipop.
> 5. Fixes to action button insets, and the stacking algorithm. Thanks [plusCubed](https://github.com/plusCubed)!
> 6. Major padding/margin fixes for using an icon with list dialogs.

###### Version 0.4.0

> 1. Bug fixes and improvements throughout
> 2. Action button selectors have rounded corners
> 3. Global theming capabilities. Override the accent color used for action buttons, titles, and content from your Activity theme. See the [Global Theming](#global-theming) section below.

###### Version 0.3.5 – 0.3.6

> 1. Bug fixes.
> 2. Button stacking algorithm fixes.

###### Version 0.3.3 – 0.3.4

> 1. Crash fix when updating list items after they had previously been set.
> 2. The ability to set the content color when constructing a dialog builder.

###### Version 0.3.2

> 1. Large performance improvements for list dialogs, thanks [hzsweers](https://github.com/hzsweers)! The item processor API was switched with custom list adapters, see the sample for an example.
> 2. Other padding and logic fixes.

###### Version 0.3.1

> 1. Global theming! A single attribute can be added to your Activity theme to make all dialogs dark. See the [Global Theming](#global-theming) section below.

###### Version 0.3.0

> 1. `MaterialDialogCompat` allows easy migration from use of `AlertDialog` (see below).
> 2. Convenience `show()` method in Builder, to skip call to `build()`.
> 3. Various important fixes from pull requests and the maintainer.

###### Version 0.2.0

> 1. Action buttons must be explicitly shown by setting text to them. The buttons will be hidden in any dialog type if no text is passed for them. This also allows you to display neutral or negative action buttons individually without relying on positive text.
> 2. List dialogs now use CharSequence arrays rather than String arrays.
> 3. Other bug fixes are included.