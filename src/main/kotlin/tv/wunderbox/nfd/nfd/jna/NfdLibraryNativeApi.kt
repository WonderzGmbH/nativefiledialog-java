package tv.wunderbox.nfd.nfd.jna

import com.sun.jna.*
import com.sun.jna.ptr.PointerByReference

interface NfdLibraryNativeApi {
    /**
     * Initialize NFD - call this for every thread that might use NFD, before calling any other NFD
     * functions on that thread
     */
    fun NFD_Init(
    ): NfdResult

    /** Call this to de-initialize NFD, if NFD_Init returned NFD_OKAY */
    fun NFD_Quit()

    /**
     * Single file open dialog
     *
     * It is the caller's responsibility to free `outPath` via NFD_FreePathN() if
     * this function returns NFD_OKAY.
     *
     * If filterCount is zero, filterList is ignored (you can use NULL)
     * If defaultPath is NULL, the operating system will decide
     */
    fun NFD_OpenDialogN(
        outPath: PointerByReference,
        filterList: Pointer?,
        filterCount: NativeLong,
        defaultPath: Pointer?,
    ): NfdResult

    /**
     * Multiple file open dialog
     *
     * It is the caller's responsibility to free `outPaths` via NFD_PathSet_Free() if
     * this function returns NFD_OKAY.
     *
     * If filterCount is zero, filterList is ignored (you can use NULL)
     * If defaultPath is NULL, the operating system will decide
     */
    fun NFD_OpenDialogMultipleN(
        outPaths: PointerByReference,
        filterList: Pointer?,
        filterCount: NativeLong,
        defaultPath: Pointer?,
    ): NfdResult

    /**
     * Save dialog
     *
     * It is the caller's responsibility to free `outPath` via NFD_FreePathN() if
     * this function returns NFD_OKAY.
     *
     * If filterCount is zero, filterList is ignored (you can use NULL)
     * If defaultPath is NULL, the operating system will decide
     */
    fun NFD_SaveDialogN(
        outPath: PointerByReference,
        filterList: Pointer?,
        filterCount: NativeLong,
        defaultPath: Pointer?,
        defaultName: Pointer?,
    ): NfdResult

    /**
     * Select folder dialog
     *
     * It is the caller's responsibility to free `outPath` via NFD_FreePathN() if
     * this function returns NFD_OKAY.
     *
     * If defaultPath is NULL, the operating system will decide
     */
    fun NFD_PickFolderN(outPath: PointerByReference, defaultPath: Pointer?): NfdResult

    fun NFD_PathSet_Free(pathSet: Pointer)

    fun NFD_FreePathN(filePath: PointerByReference)

    /**
     * Gets the number of entries stored in pathSet
     *
     * note that some paths might be invalid (NFD_ERROR will be returned by NFD_PathSet_GetPath), so we
     * might not actually have this number of usable paths
     */
    fun NFD_PathSet_GetCount(
        pathSet: Pointer,
        count: Pointer,
    ): NfdResult

    /**
     * Gets the UTF-8 path at offset index
     *
     * It is the caller's responsibility to free `outPath` via NFD_PathSet_FreePathN() if this function
     * returns NFD_OKAY
     */
    fun NFD_PathSet_GetPathN(
        pathSet: Pointer,
        index: Long,
        outPath: PointerByReference,
    ): NfdResult
}
