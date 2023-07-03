package tv.wunderbox.nfd

public sealed interface FileDialogResult<out T> {
    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = this !is Failure

    public data class Failure(
        val error: FileDialog.Error,
    ) : FileDialogResult<Nothing>

    public data class Success<T>(
        val value: T,
    ) : FileDialogResult<T>
}
