package tv.wunderbox.nfd

sealed interface FileDialogResult<out T> {
    data class Failure(
        val error: FileDialog.Error,
    ) : FileDialogResult<Nothing>

    data class Success<T>(
        val value: T,
    ) : FileDialogResult<T>
}
