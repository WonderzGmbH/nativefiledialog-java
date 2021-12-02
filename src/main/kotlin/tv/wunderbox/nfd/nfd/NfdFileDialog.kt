package tv.wunderbox.nfd.nfd

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.sun.jna.*
import com.sun.jna.ptr.PointerByReference
import org.apache.commons.lang3.SystemUtils
import tv.wunderbox.nfd.FileDialog
import tv.wunderbox.nfd.nfd.jna.*
import java.io.File

class NfdFileDialog : FileDialog {
    override fun save(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
        defaultName: String?,
    ): Either<FileDialog.Error, File> = pick { nfd, outPathPointer ->
        nfd.NFD_SaveDialogN(
            outPath = outPathPointer,
            filterList = filters
                .takeUnless { it.isEmpty() }
                ?.asMemory()
                ?.let(::register),
            filterCount = NativeLong(filters.size.toLong()),
            defaultPath = defaultPath?.takeIf { it.isNotBlank() }
                ?.asMemory()
                ?.let(::register),
            defaultName = defaultName?.takeIf { it.isNotBlank() }
                ?.asMemory()
                ?.let(::register),
        )
    }

    override fun pickFile(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
    ): Either<FileDialog.Error, File> = pick { nfd, outPathPointer ->
        nfd.NFD_OpenDialogN(
            outPath = outPathPointer,
            filterList = filters
                .takeUnless { it.isEmpty() }
                ?.asMemory()
                ?.let(::register),
            filterCount = NativeLong(filters.size.toLong()),
            defaultPath = defaultPath?.takeIf { it.isNotBlank() }
                ?.asMemory()
                ?.let(::register),
        )
    }

    override fun pickFileMany(
        filters: List<FileDialog.Filter>,
        defaultPath: String?,
    ): Either<FileDialog.Error, List<File>> {
        val nfd = NfdLibraryNative.get() // may throw an exception
        nfd.NFD_Init()

        val outPathIteratorPointer = PointerByReference()
        val scope = DisposableScope()
        val result = try {
            nfd.NFD_OpenDialogMultipleN(
                outPaths = outPathIteratorPointer,
                filterList = filters
                    .takeUnless { it.isEmpty() }
                    ?.asMemory()
                    ?.let(scope::register),
                filterCount = NativeLong(filters.size.toLong()),
                defaultPath = defaultPath?.takeIf { it.isNotBlank() }
                    ?.asMemory()
                    ?.let(scope::register),
            )
        } finally {
            scope.dispose()
        }
        when (result) {
            NfdResult.NFD_CANCEL -> return FileDialog.Error.CANCEL.left()
            NfdResult.NFD_ERROR -> return FileDialog.Error.ERROR.left()
            else -> {
                // Continue.
            }
        }

        val countPointer = Memory(Native.LONG_SIZE.toLong())
        val countResult = nfd.NFD_PathSet_GetCount(
            pathSet = outPathIteratorPointer.value,
            count = countPointer,
        )
        if (countResult != NfdResult.NFD_OKAY)
            return FileDialog.Error.ERROR.left()
        val count = try {
            if (SystemUtils.IS_OS_LINUX) {
                countPointer
                    .getInt(0L)
            } else {
                // everything that is not a linux expects a long type
                countPointer
                    .getLong(0L)
                    .toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return FileDialog.Error.ERROR.left()
        }

        val files = (0 until count)
            .map { index ->
                val outPathPointer = PointerByReference()
                val pathResult = nfd.NFD_PathSet_GetPathN(
                    pathSet = outPathIteratorPointer.value,
                    index = index.toLong(),
                    outPath = outPathPointer,
                )

                if (pathResult != NfdResult.NFD_OKAY) {
                    return@map null
                }

                val outPath = try {
                    outPathPointer.value
                        // obtain the string object
                        .getString(0L)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return FileDialog.Error.ERROR.left()
                } finally {
                    nfd.NFD_FreePathN(
                        filePath = outPathPointer,
                    )
                }
                outPath.let(::File)
            }
            .filterNotNull()

        nfd.NFD_PathSet_Free(outPathIteratorPointer.value)
        nfd.NFD_Quit()

        return files.right()
    }

    override fun pickDirectory(
        defaultPath: String?,
    ): Either<FileDialog.Error, File> = pick { nfd, outPathPointer ->
        nfd.NFD_PickFolderN(
            outPath = outPathPointer,
            defaultPath = defaultPath?.takeIf { it.isNotBlank() }
                ?.asMemory()
                ?.let(::register),
        )
    }

    private fun pick(
        block: DisposableScope.(NfdLibraryNativeApi, PointerByReference) -> NfdResult,
    ): Either<FileDialog.Error, File> {
        val nfd = NfdLibraryNative.get() // may throw an exception
        nfd.NFD_Init()

        val outPathPointer = PointerByReference()
        val scope = DisposableScope()
        val result = try {
            block(scope, nfd, outPathPointer)
        } finally {
            scope.dispose()
        }
        when (result) {
            NfdResult.NFD_CANCEL -> return FileDialog.Error.CANCEL.left()
            NfdResult.NFD_ERROR -> return FileDialog.Error.ERROR.left()
            else -> {
                // Continue.
            }
        }

        val outPath = try {
            outPathPointer.value
                // obtain the string object
                .getString(0L)
        } catch (e: Exception) {
            e.printStackTrace()
            return FileDialog.Error.ERROR.left()
        } finally {
            nfd.NFD_FreePathN(
                filePath = outPathPointer,
            )
        }

        nfd.NFD_Quit()

        val outFile = File(outPath)
        return outFile.right()
    }
}
