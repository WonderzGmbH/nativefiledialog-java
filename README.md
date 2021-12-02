# Native File Dialog Java

A Kotlin/Java wrapper around the small C library by Bernard Teo ([btzy/nativefiledialog-extended](https://github.com/btzy/nativefiledialog-extended)). 
As a fallback it uses default AWT file picker.

*A library is a perfect fit for the Jetpack Compose for Desktop.*

### API

```kotlin
interface FileDialog {
    fun save(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
        defaultName: String? = null,
    ): Either<Error, File>

    fun pickFile(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): Either<Error, File>

    fun pickFileMany(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): Either<Error, List<File>>

    fun pickDirectory(
        defaultPath: String? = null,
    ): Either<Error, File>
}
```

#### Example usage
```kotlin
val fileDialog = FileDialog.default(awtComponent)
fileDialog.pickDirectory() // returns Either<Error, File>
```
   
