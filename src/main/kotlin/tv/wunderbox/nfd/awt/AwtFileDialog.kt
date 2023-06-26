package tv.wunderbox.nfd.awt

import tv.wunderbox.nfd.FileDialog
import tv.wunderbox.nfd.FileDialogResult
import java.awt.Component
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

/**
 * @author Artem Chepurnoy
 */
class AwtFileDialog(
    private val window: Component,
    private val title: String? = null,
) : FileDialog {
    override fun save(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
        defaultName: String?,
    ): FileDialogResult<File> {
        val chooser = createJFileChooser(
            filters = filters,
            defaultPath = defaultPath,
            defaultName = defaultName,
        )
        return chooser.showForResult(
            window = window,
            open = JFileChooser::showSaveDialog,
        ) {
            selectedFile
        }
    }

    override fun pickFile(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
    ): FileDialogResult<File> {
        val chooser = createJFileChooser(
            filters = filters,
            defaultPath = defaultPath,
        )
        return chooser.showForResult(
            window = window,
            open = JFileChooser::showOpenDialog,
        ) {
            selectedFile
        }
    }

    override fun pickFileMany(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
    ): FileDialogResult<List<File>> {
        val chooser = createJFileChooser(
            filters = filters,
            defaultPath = defaultPath,
        ) {
            isMultiSelectionEnabled = true
        }
        return chooser.showForResult(
            window = window,
            open = JFileChooser::showOpenDialog,
        ) {
            selectedFiles?.toList()?.takeUnless { it.isEmpty() }
        }
    }

    private inline fun createJFileChooser(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
        defaultName: String? = null,
        block: JFileChooser.() -> Unit = {},
    ) = JFileChooser().apply {
        title?.let(::setDialogTitle)

        val dir = defaultPath?.let(::File)
            ?.let { f ->
                f.takeIf { it.isDirectory }
                    ?: f.parentFile
            }
            ?: File(".")
        currentDirectory = dir
        if (defaultName != null) {
            val file = dir.resolve(defaultName)
            selectedFile = file
        }
        filters.forEach { filter ->
            val awtFilter = object : FileFilter() {
                override fun accept(file: File): Boolean = file.extension in filter.extensions

                override fun getDescription(): String = filter.title
            }
            addChoosableFileFilter(awtFilter)
        }

        fileSelectionMode = JFileChooser.FILES_ONLY

        block()
    }

    override fun pickDirectory(defaultPath: String?): FileDialogResult<File> {
        val chooser = JFileChooser().apply {
            title?.let(::setDialogTitle)

            val file = defaultPath?.let(::File)
            if (file != null) {
                currentDirectory = file
            }
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        return chooser.showForResult(
            window = window,
            open = JFileChooser::showOpenDialog,
        ) {
            val outFile = chooser.selectedFile.takeIf { it.isDirectory }
                ?: chooser.currentDirectory
            outFile
        }
    }

    private inline fun <T : Any> JFileChooser.showForResult(
        window: Component,
        open: JFileChooser.(Component) -> Int,
        handle: JFileChooser.() -> T?,
    ): FileDialogResult<T> = when (open(window)) {
        JFileChooser.APPROVE_OPTION -> {
            val result = handle()
            if (result != null) {
                FileDialogResult.Success(result)
            } else {
                val why = FileDialog.Error.CANCEL
                FileDialogResult.Failure(why)
            }
        }

        JFileChooser.CANCEL_OPTION -> FileDialogResult.Failure(FileDialog.Error.CANCEL)
        JFileChooser.ERROR_OPTION -> FileDialogResult.Failure(FileDialog.Error.ERROR)
        else -> error("Unknown file chooser result.")
    }
}
