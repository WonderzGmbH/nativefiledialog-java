package tv.wunderbox.nfd

public sealed interface FileDialogResult<out T> {
    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = this !is Failure

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    public val isFailure: Boolean get() = this is Failure

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or `null`
     * if it is [failure][isFailure].
     */
    public fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    public data class Failure(
        val error: FileDialog.Error,
    ) : FileDialogResult<Nothing>

    public data class Success<T>(
        val value: T,
    ) : FileDialogResult<T>
}
