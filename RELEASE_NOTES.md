3.2.1

* Fixed the module-name given to the Kotlin compiler for each Gradle module, should fix
extension function resolution issues.

---

3.2.0

* Dependency upgrades.
* Reduce single/multi choice list dialog margin between text and controls.
* Fix `updateTextColor(Int)` on action buttons not always persisting. See #1783.
* Fix corner radius not working when views have a background. See #1840.
* All dialogs will have a default corner radius of 4dp. See #1909.
* Non-cancelable bottom sheet dialogs cannot be swiped to dismiss.
* Other minor changes.
