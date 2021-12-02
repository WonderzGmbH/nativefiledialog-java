package tv.wunderbox.nfd.nfd.jna

import com.sun.jna.FromNativeContext
import com.sun.jna.NativeMapped

enum class NfdResult : NativeMapped {
    NFD_ERROR, /* programmatic error */
    NFD_OKAY, /* user pressed okay, or successful return */
    NFD_CANCEL; /* user pressed cancel */

    override fun fromNative(nativeValue: Any?, context: FromNativeContext?): Any {
        return values()[nativeValue as Int]
    }

    override fun toNative(): Any = ordinal

    override fun nativeType(): Class<*> = Int::class.java
}
