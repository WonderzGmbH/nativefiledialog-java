package tv.wunderbox.nfd.nfd.jna

import com.sun.jna.Memory

internal class DisposableMemory(
    val memory: Memory,
    val dispose: () -> Unit,
)
