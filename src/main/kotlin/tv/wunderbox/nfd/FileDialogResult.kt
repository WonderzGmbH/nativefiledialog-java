package tv.wunderbox.nfd

public sealed interface FileDialogResult<out T> {
    public data class Failure(
        val error: FileDialog.Error,
    ) : FileDialogResult<Nothing>

    public data class Success<T>(
        val value: T,
    ) : FileDialogResult<T>
}
