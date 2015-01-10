# Changelog

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