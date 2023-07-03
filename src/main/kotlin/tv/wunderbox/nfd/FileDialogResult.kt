package tv.wunderbox.nfd

public sealed interface FileDialogResult<out T> {
    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    public val isSuccess: Boolean get() = this is Success

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

    public class Failure(
        public val error: FileDialog.Error,
    ) : FileDialogResult<Nothing> {
        override fun equals(other: Any?): Boolean = other is Failure && error == other.error
        override fun hashCode(): Int = error.hashCode()
        override fun toString(): String = "Failure(${error.name})"
    }

    public class Success<T>(
        public val value: T,
    ) : FileDialogResult<T> {
        override fun equals(other: Any?): Boolean = other is Success<*> && value == other.value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Success(value=$value)"
    }
}
