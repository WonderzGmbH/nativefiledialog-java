[![Maven Central](https://maven-badges.herokuapp.com/maven-central/tv.wunderbox/nativefiledialog/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tv.wunderbox/nativefiledialog)

# Native File Dialog Java

A Kotlin/Java wrapper around the small C library by Bernard Teo ([btzy/nativefiledialog-extended](https://github.com/btzy/nativefiledialog-extended)). 
As a fallback it uses default AWT file picker.

*A library is a perfect fit for the Jetpack Compose for Desktop.*

Supported platforms: 
- Windows;
- MacOS;
- Linux (GTK).

### Download

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation("tv.wunderbox:nativefiledialog:$nativeFileDialogVersion")
}
```

### API

```kotlin
interface FileDialog {
    fun save(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
        defaultName: String? = null,
    ): FileDialogResult<File>

    fun pickFile(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): FileDialogResult<File>

    fun pickFileMany(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): FileDialogResult<List<File>>

    fun pickDirectory(
        defaultPath: String? = null,
    ): FileDialogResult<File>
}
```

#### Example usage
```kotlin
val fileDialog = FileDialog.default(awtComponent)
val result = fileDialog.pickFile() // returns FileDialogResult<File>
when (result) {
    is FileDialogResult.Success -> {
        val file = result.value
        // Handle success
    }
    is FileDialogResult.Failure -> {
        // Handle failure
    }
}
```

##### Create picker directly
Internally there are two different implementations available,
instead of using the default `NfdFileDialog` with `AwtFileDialog` as 
a fallback, you can use only one:

```kotlin
val nfdFileDialog = NfdFileDialog()
val awtFileDialog = AwtFileDialog(awtComponent)
```

When using [Kotlin Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) the `awtComponent` variable is a `ComposeWindow` object that might be obtained from `androidx.compose.ui.window.WindowScope`. 
