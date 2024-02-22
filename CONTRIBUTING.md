## Contributing

## Developing

### Software Requirements
You should have the following things installed:
* Git
* Java 17 - should be as unmodified as possible (Recommended: [Eclipse Adoptium](https://adoptium.net/temurin/releases/))
* Gradle (shipped inside the repo as Gradle Wrapper - also available inside Intellij)

### Recommended setup
* Install ``IntelliJ`` (Community Edition is sufficient)
  * Install the following plugins:
    * [Save Actions](https://plugins.jetbrains.com/plugin/22113) - Provides save actions, like running the formatter or adding ``final`` to fields
    * [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) - CodeStyle/CodeAnalysis
    * [Checkstyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) - CodeStyle/CodeAnalysis
    * [Plugin DevKit](https://plugins.jetbrains.com/plugin/22851) - Intellij Plugin development
  * Import the project
  * Ensure that everything is encoded in ``UTF-8``
  * Ensure that the JDK/Java-Version is correct

## Development environment

<i>See also [JetBrains Docs for developing IntelliJ Plugins](https://plugins.jetbrains.com/docs/intellij/developing-plugins.html)</i>

The plugin is built with gradle, but you don't need to install it if you build with the Intellij gradle plugin (check out the [prerequisites](https://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)). If you don't intend to use the Intellij gradle plugin, you can use native gradle (replace `./gradlew` by `gradle`).

Start idea and import the `build.gradle` file with "File > Open". Then in the "Import Project from Gradle" window, make sure you check "Use gradle 'wrapper' task configuration" before clicking "Finish". You now have a gradle wrapper installed (`gradlew`) that you can use on the command line to generate idea folders:

```bash
# Initialize idea folders
./gradlew cleanIdea idea
```

Intellij should refresh and the project is now configured as a gradle project. You can find Intellij gradle tasks in "Gradle > Gradle projects > intellij-plugin-save-actions > Tasks > intellij". To run the plugin, use the `runIde` task:

```bash
# Run the plugin (starts new idea)
./gradlew runIde
```

<i>Based on the [original documentation](https://github.com/dubreuia/intellij-plugin-save-actions/blob/main/CONTRIBUTING.md)</i>

## Releasing [![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/intellij-plugin-save-actions/release.yml?branch=master)](https://github.com/xdev-software/intellij-plugin-save-actions/actions/workflows/release.yml)

Before releasing:
* Consider doing a [test-deployment](https://github.com/xdev-software/intellij-plugin-save-actions/actions/workflows/test-deploy.yml?query=branch%3Adevelop) before actually releasing.
* Check the [changelog](CHANGELOG.md)

If the ``develop`` is ready for release, create a pull request to the ``master``-Branch and merge the changes

When the release is finished do the following:
* Merge the auto-generated PR (with the incremented version number) back into the ``develop``

