package io.github.aliernfrog.pftool_shared.impl

import android.content.Context
import io.github.aliernfrog.pftool_shared.data.MapActionResult
import io.github.aliernfrog.pftool_shared.enum.MapImportedState
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import io.github.aliernfrog.shared.impl.ContextUtils

interface IMapFile {
    val file: FileWrapper
    val contextUtils: ContextUtils

    val name: String
        get() = file.nameWithoutExtension

    val fileName: String
        get() = file.name

    val path: String
        get() = file.path

    val size: Long
        get() = file.size

    val lastModified: Long
        get() = file.lastModified

    val importedState: MapImportedState

    val thumbnailModel: Any?
        get() = getThumbnailPainterModel() ?: getThumbnailFile()?.painterModel

    val readableSize: String
        get() = "${size / 1024} KB"

    val readableLastModified: String
        get() = contextUtils.stringFunction { context ->
            PFToolSharedUtil.lastModifiedFromLong(this.lastModified, context)
        }

    val details: String
        get() = "$readableSize | $readableLastModified"


    fun rename(newName: String): MapActionResult

    fun duplicate(context: Context, newName: String): MapActionResult

    fun import(context: Context, withName: String = this.name): MapActionResult

    fun export(context: Context, withName: String = this.name): MapActionResult

    suspend fun exportToCustomLocation(context: Context, withName: String): MapActionResult

    fun delete() = file.delete()

    /**
     * Returns the thumbnail file.
     * This must return a cached object to avoid performance issues.
     */
    fun getThumbnailFile(): FileWrapper?

    /**
     * Returns the thumbnail painter model, priotized over [getThumbnailFile] if not null.
     * This can return a value even if [getThumbnailFile] returns null, such as when the thumbnail
     * is from a non-file.
     * This must return a cached object to avoid performance issues.
     */
    fun getThumbnailPainterModel(): Any?

    suspend fun runInIOThreadSafe(block: suspend () -> Unit)
}