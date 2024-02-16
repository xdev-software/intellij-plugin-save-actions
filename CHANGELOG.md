## 1.1.1
* Shortened plugin name - nem name: "Save Actions X"

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

