# Contributing

## Development environment

The plugin is built with gradle, but you don't need to install it if you build with the Intellij gradle plugin (check out the [prerequisites](https://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html)).  If you don't intend to use the Intellij gradle plugin, you can use native gradle (replace `./gradlew` by `gradle`).

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

