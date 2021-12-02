package tv.wunderbox.nfd.nfd.jna

import com.sun.jna.Memory
import com.sun.jna.Native
import tv.wunderbox.nfd.FileDialog

internal fun List<FileDialog.Filter>.asMemory() = kotlin.run {
    val x = flatMap { filter ->
        val title = filter.title
            .asMemory()
        val extensions = filter.extensions
            .joinToString(",")
            .asMemory()
        listOf(title, extensions)
    }

    val memory = Memory(Native.POINTER_SIZE * x.size * 1L)
    var offset = 0L
    x.forEach { m ->
        memory.setPointer(offset, m.memory)
        offset += Native.POINTER_SIZE
    }

    DisposableMemory(
        memory = memory,
        dispose = {
            // dispose every little string that we have
            x.forEach {
                it.dispose()
            }
        },
    )
}
