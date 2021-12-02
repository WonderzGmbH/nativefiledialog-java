package tv.wunderbox.nfd.nfd.jna

import com.sun.jna.*
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Files

private const val NFD_RES_FILENAME_WIN = "libnfd_win.dll"
private const val NFD_RES_FILENAME_LINUX = "libnfd_linux.so"
private const val NFD_RES_FILENAME_MAC = "libnfd_mac.dylib"

private const val NFD_OS_DIR_PREFIX = "libnfd"
private const val NFD_OS_FILE_FILENAME = "libnfd"

private val libraryFile by lazy {
    val filename = when {
        SystemUtils.IS_OS_WINDOWS ->
            NFD_RES_FILENAME_WIN
        SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX ->
            NFD_RES_FILENAME_MAC
        SystemUtils.IS_OS_LINUX ->
            NFD_RES_FILENAME_LINUX
        else -> throw IllegalArgumentException()
    }
    extractLibrary(filename)
}

private class Extractor

private fun extractLibrary(filename: String): File {
    val outDir = Files
        .createTempDirectory(NFD_OS_DIR_PREFIX)
        .toFile()
    val outFile = outDir
        .resolve(NFD_OS_FILE_FILENAME)
    outFile.deleteOnExit()
    // Copy the ffmpeg into the
    // output file.
    outFile.outputStream().use {
        Extractor::class.java.classLoader
            .getResourceAsStream(filename)!!
            .copyTo(it)
    }
    outFile.setExecutable(true, false)
    return outFile
}

interface NfdLibraryNative : Library, NfdLibraryNativeApi {
    companion object {
        @Volatile
        private var instance: NfdLibraryNativeApi? = null

        fun get(): NfdLibraryNativeApi {
            if (instance == null) {
                synchronized(NfdLibraryNative::class.java) {
                    if (instance == null) {
                        instance = Native.load(libraryFile.canonicalPath, NfdLibraryNative::class.java) as NfdLibraryNative
                    }
                }
            }
            return instance!!
        }
    }
}
