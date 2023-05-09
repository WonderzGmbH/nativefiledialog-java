package tv.wunderbox.nfd

import tv.wunderbox.nfd.awt.AwtFileDialog
import tv.wunderbox.nfd.nfd.NfdFileDialog
import java.awt.Component
import java.io.File

public interface FileDialog {
    public companion object {
        /**
         * Default implementation tries to open the native file dialog, and
         * falls back to the AWT file dialog if that.
         */
        public fun default(
            window: Component,
        ): FileDialog = kotlin.run {
            val nfd = NfdFileDialog()
            val awt = AwtFileDialog(
                window = window,
            )
            nfd.fallbackWith(awt)
        }
    }

    public fun save(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
        defaultName: String? = null,
    ): FileDialogResult<File>

    public fun pickFile(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): FileDialogResult<File>

    public fun pickFileMany(
        filters: List<Filter> = emptyList(),
        defaultPath: String? = null,
    ): FileDialogResult<List<File>>

    public fun pickDirectory(
        defaultPath: String? = null,
    ): FileDialogResult<File>

    public enum class Error {
        ERROR,
        CANCEL,
    }

    public data class Filter(
        val title: String,
        val extensions: List<String>,
    )
}

public fun FileDialog.fallbackWith(nfd: FileDialog): FileDialog =
    object : FileDialog {
        override fun save(
            filters: List<FileDialog.Filter>,
            defaultPath: String?,
            defaultName: String?,
        ): FileDialogResult<File> = try {
            this@fallbackWith.save(
                filters = filters,
                defaultPath = defaultPath,
                defaultName = defaultName,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            // otherwise fallback
            nfd.save(
                filters = filters,
                defaultPath = defaultPath,
                defaultName = defaultName,
            )
        }

        override fun pickFile(
            filters: List<FileDialog.Filter>,
            defaultPath: String?,
        ): FileDialogResult<File> = try {
            this@fallbackWith.pickFile(
                filters = filters,
                defaultPath = defaultPath,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            // otherwise fallback
            nfd.pickFile(
                filters = filters,
                defaultPath = defaultPath,
            )
        }

        override fun pickFileMany(
            filters: List<FileDialog.Filter>,
            defaultPath: String?,
        ): FileDialogResult<List<File>> = try {
            this@fallbackWith.pickFileMany(
                filters = filters,
                defaultPath = defaultPath,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            // otherwise fallback
            nfd.pickFileMany(
                filters = filters,
                defaultPath = defaultPath,
            )
        }

        override fun pickDirectory(
            defaultPath: String?,
        ): FileDialogResult<File> = try {
            this@fallbackWith.pickDirectory(
                defaultPath = defaultPath,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            // otherwise fallback
            nfd.pickDirectory(
                defaultPath = defaultPath,
            )
        }
    }
