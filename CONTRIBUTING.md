## Contributing

We would absolutely love to get the community involved, and we welcome any form of contributions – comments and questions on different communication channels, issues and pull request and anything that you build and share using our components.

### Communication channels
* Communication is primarily done using issues.
* If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).
* As a last resort measure or on otherwise important matter you may also [contact us directly](https://xdev.software/en/about-us/contact).

### Ways to help
* **Report bugs**<br/>Create an issue or send a pull request
* **Send pull requests**<br/>If you want to contribute code, check out the development instructions below.
  * However when contributing new features, please first discuss the change you wish to make via issue with the owners of this repository before making a change. Otherwise your work might be rejected and your effort was pointless.

We also encourage you to read the [contribution instructions by GitHub](https://docs.github.com/en/get-started/quickstart/contributing-to-projects).

## Developing

### Software Requirements
You should have the following things installed:
* Git
* Java 21 - should be as unmodified as possible (Recommended: [Eclipse Adoptium](https://adoptium.net/temurin/releases/))
* Gradle (shipped inside the repo as Gradle Wrapper - also available inside IntelliJ)

### Recommended setup
* Install ``IntelliJ`` (Community Edition is sufficient)
  * Install the following plugins:
    * [Save Actions](https://plugins.jetbrains.com/plugin/22113) - Provides save actions, like running the formatter or adding ``final`` to fields
    * [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) - CodeStyle/CodeAnalysis
    * [Checkstyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) - CodeStyle/CodeAnalysis
    * [Plugin DevKit](https://plugins.jetbrains.com/plugin/22851) - IntelliJ Plugin development
  * Import the project
  * Ensure that everything is encoded in ``UTF-8``
  * Ensure that the JDK/Java-Version is correct

## Development environment

<i>See also [JetBrains Docs for developing IntelliJ Plugins](https://plugins.jetbrains.com/docs/intellij/developing-plugins.html)</i>

The plugin is built with gradle, but you don't need to install it if you build with the IntelliJ gradle plugin (check out the [prerequisites](https://plugins.jetbrains.com/docs/intellij/plugin-required-experience.html)). If you don't intend to use the IntelliJ gradle plugin, you can use native gradle (replace `./gradlew` by `gradle`).

Start idea and import the `build.gradle` file with "File > Open". Then in the "Import Project from Gradle" window, make sure you check "Use gradle 'wrapper' task configuration" before clicking "Finish". You now have a gradle wrapper installed (`gradlew`) that you can use on the command line to generate idea folders:

```bash
# Initialize idea folders
./gradlew cleanIdea idea
```

IntelliJ should refresh and the project is now configured as a gradle project. You can find IntelliJ gradle tasks in "Gradle > Gradle projects > template-placeholder > Tasks > intellij". To run the plugin, use the `runIde` task:

```bash
# Run the plugin (starts new idea)
./gradlew runIde
```

## Releasing [![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/template-placeholder/release.yml?branch=master)](https://github.com/xdev-software/template-placeholder/actions/workflows/release.yml)

Before releasing:
* Consider doing a [test-deployment](https://github.com/xdev-software/template-placeholder/actions/workflows/test-deploy.yml?query=branch%3Adevelop) before actually releasing.
* Check the [changelog](CHANGELOG.md)

If the ``develop`` is ready for release, create a pull request to the ``master``-Branch and merge the changes

When the release is finished do the following:
* Merge the auto-generated PR (with the incremented version number) back into the ``develop``

