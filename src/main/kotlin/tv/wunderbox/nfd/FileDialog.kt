package tv.wunderbox.nfd

import arrow.core.Either
import tv.wunderbox.nfd.awt.AwtFileDialog
import tv.wunderbox.nfd.nfd.NfdFileDialog
import java.awt.Component
import java.io.File

interface FileDialog {
    companion object {
        /**
         * Default implementation tries to open the native file dialog, and
         * falls back to the AWT file dialog if that.
         */
        fun default(
            window: Component,
        ): FileDialog = kotlin.run {
            val nfd = NfdFileDialog()
            val awt = AwtFileDialog(
                window = window,
            )
            nfd.fallbackWith(awt)
        }
    }

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

    enum class Error {
        ERROR,
        CANCEL,
    }

    data class Filter(
        val title: String,
        val extensions: List<String>,
    )
}

fun FileDialog.fallbackWith(nfd: FileDialog): FileDialog =
    object : FileDialog {
        override fun save(
            filters: List<FileDialog.Filter>,
            defaultPath: String?,
            defaultName: String?,
        ): Either<FileDialog.Error, File> = try {
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
        ): Either<FileDialog.Error, File> = try {
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
        ): Either<FileDialog.Error, List<File>> = try {
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
        ): Either<FileDialog.Error, File> = try {
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
