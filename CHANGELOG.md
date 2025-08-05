## 1.4.2
* Fix storage deserialization crash on unknown actions value #273

## 1.4.1
* Fix ``Add class qualifier to static member access outside declaring class`` not working correctly for ``switch`` statements #263

## 1.4.0
* Dropped support for IntelliJ versions < 2024.3
  * This is required to fix a few deprecations and remove some workarounds #171

## 1.3.1
* Fix IDE hang when projects with different "Process files asynchronously" are open #160

## 1.3.0
* Make it possible to run processors asynchronously #130
  * This way the UI should be more responsive when processing a lot of files
  * May break processors that interact with the UI e.g. when showing dialogs
* Don't process files during project load #145
  * This should cause less race conditions due to partial project initialization
  * Only active on IntelliJ < 2024.3 as [the underlying problem was fixed in IntelliJ 2024.3](https://github.com/JetBrains/intellij-community/commit/765caa71175d0a67a54836cf840fae829da590d9)

## 1.2.4
* Dropped support for IntelliJ versions < 2024.2
* Removed deprecated code that was only required for older IDE versions

## 1.2.3
* Fix "run on multiple files" not working when the file is not a text file #129

## 1.2.2
* Workaround scaling problem on "New UI" [#26](https://github.com/xdev-software/intellij-plugin-template/issues/26)

## 1.2.1
* Fixed ``ToggleAnAction must override getActionUpdateThread`` warning inside IntelliJ 2024+
* Dropped support for IntelliJ versions < 2023.2

## 1.2.0
* Run GlobalProcessors (e.g. Reformat) last so that code is formatted correctly #90
* Dropped support for IntelliJ versions < 2023

## 1.1.1
* Shortened plugin name - new name: "Save Actions X"
* Updated assets

## 1.1.0
* Removed "Remove unused suppress warning annotation"
  * This option never worked #64
  * Allows usage of the plugin with IntelliJ IDEA 2024+ #63
  * If you used this option you should remove the line ``<option value="suppressAnnotation" />`` inside ``saveactions_settings.xml``
* Allow compilation with Java 21

## 1.0.5
* Fixed ``Add class qualifier to static member access outside declaring class`` not working in combination with Qodana plugin #25

## 1.0.4
* Fixed pluginIcon being not displayed #35
* Improved support of Android Studio (until a 2023 version is released) #27

## 1.0.3
* Fixed problem in combination with Qodana plugin #25
* Improved compatibility and cleaned up code #27

## 1.0.2
* Fixed missing display name which causes an error when multiple configurable plugins are installed #20

## 1.0.1
* Fixed ``Change visibility of field or method to lower access`` not working #14

## 1.0.0
<i>Initial release</i>
* Fork of [dubreuia/intellij-plugin-save-actions](https://github.com/dubreuia/intellij-plugin-save-actions) and [fishermans/intellij-plugin-save-actions](https://github.com/fishermans/intellij-plugin-save-actions)
  * ⚠️ This plugin is not compatible with the old/deprecated/forked one.<br/>Please ensure that the old plugin is uninstalled.
* Rebrand
* Updated copy pasted classes from IDEA

