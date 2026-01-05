package io.github.aliernfrog.pftool_shared.data

import android.os.Parcelable
import io.github.aliernfrog.pftool_shared.util.extension.size
import io.github.aliernfrog.pftool_shared.util.staticutil.PFToolSharedUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class ServiceFile(
    val name: String,
    val path: String,
    val parentPath: String?,
    val size: Long,
    val lastModified: Long,
    val isFile: Boolean
): Parcelable {
    @IgnoredOnParcel
    val nameWithoutExtension = PFToolSharedUtil.removeExtension(this.name)

    companion object {
        fun fromFile(file: File): ServiceFile {
            return ServiceFile(
                name = file.name,
                path = file.path,
                parentPath = file.parent,
                size = file.size,
                lastModified = file.lastModified(),
                isFile = file.isFile
            )
        }
    }
}