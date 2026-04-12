package io.github.aliernfrog.pftool_shared.util.staticutil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Log
import android.webkit.URLUtil
import androidx.core.net.toUri
import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.enum.DocumentsUIPackageMetadata
import io.github.aliernfrog.pftool_shared.util.extension.toPath
import io.github.aliernfrog.pftool_shared.util.hasAndroidDataRestrictions
import io.github.aliernfrog.shared.util.TAG
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.Locale
import kotlin.collections.indexOf

class PFToolSharedUtil {
    companion object {
        fun documentsUIRestrictsAndroidData(context: Context): Boolean {
            if (!hasAndroidDataRestrictions) return false
            val documentsUIPackage = getDocumentsUIPackageInfo(context)
            return documentsUIPackage?.let { (packageInfo, metadata) ->
                packageInfo.longVersionCode >= metadata.androidDataRestrictedVersion
            } ?: false
        }

        fun getDocumentsUIPackageInfo(context: Context): Pair<PackageInfo, DocumentsUIPackageMetadata>? {
            var result: Pair<PackageInfo, DocumentsUIPackageMetadata>? = null
            for (metadata in DocumentsUIPackageMetadata.entries) {
                try {
                    result = context.packageManager.getPackageInfo(metadata.packageName, 0) to metadata
                    break
                } catch (_: PackageManager.NameNotFoundException) {
                    // Ignore
                } catch (e: Exception) {
                    Log.e(TAG, "PFToolSharedUtil/getDocumentsUiPackageInfo: failed to get package info for ${metadata.packageName}", e)
                }
            }
            return result
        }

        fun launchDocumentsUIAppInfoPage(context: Context) {
            val packageInfo = getDocumentsUIPackageInfo(context)
            val metadata = packageInfo?.second ?: DocumentsUIPackageMetadata.GOOGLE
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = "package:${metadata.packageName}".toUri()
                context.startActivity(this)
            }
        }

        /**
         * Gets [Language] from given language code.
         * [code] must either be a language code, or language and country code split by a "-" (e.g.: en-US, en)
         *
         * @return [Language] if [code] is valid, null if it is invalid
         */
        fun getLanguageFromCode(code: String, languages: Array<String>, translationProgresses: FloatArray): Language? {
            val split = code.split("-")
            val languageCode = split.getOrNull(0) ?: return null
            val countryCode = split.getOrNull(1)
            val locale = getLocale(languageCode, countryCode)
            var translationProgress = 0f
            try {
                val index = languages.indexOf(code)
                if (index != -1) {
                    translationProgress = translationProgresses[index]
                }
            } catch (_: Exception) {}
            return Language(
                languageCode = languageCode,
                countryCode = countryCode,
                fullCode = code,
                localizedName = locale.getDisplayName(locale),
                translationProgress = translationProgress
            )
        }

        fun getLocale(language: String, country: String? = null): Locale {
            val builder = Locale.Builder().setLanguage(language)
            if (country != null) builder.setRegion(country)
            return builder.build()
        }

        fun removeExtension(path: String): String {
            val extensionIndex = path.lastIndexOf(".")
            if (extensionIndex == -1) return path
            return path.substring(0, extensionIndex)
        }

        fun getFileName(path: String, removeExtension: Boolean = false): String {
            val name = path.split("/").last()
            return if (removeExtension) removeExtension(name)
            else name
        }

        fun getFilePath(path: String): String {
            return if (path.startsWith("/")) path
            else path.toUri().toPath()
        }

        fun getUriForPath(path: String): Uri {
            return DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:"+path.removePrefix("${Environment.getExternalStorageDirectory()}/")
            )
        }

        fun getTreeUriForPath(path: String): Uri {
            return DocumentsContract.buildTreeDocumentUri(
                "com.android.externalstorage.documents",
                "primary:"+path.removePrefix("${Environment.getExternalStorageDirectory()}/")
            )
        }

        fun lastModifiedFromLong(lastModified: Long?, context: Context): String {
            val lastModifiedTime = lastModified ?: System.currentTimeMillis()
            return DateUtils.getRelativeDateTimeString(
                /* c = */ context,
                /* time = */ lastModifiedTime,
                /* minResolution = */ DateUtils.SECOND_IN_MILLIS,
                /* transitionResolution = */ DateUtils.DAY_IN_MILLIS,
                /* flags = */ 0
            ).toString()
        }

        fun copyDirectory(source: File, target: File) {
            if (!target.isDirectory) target.mkdirs()
            source.listFiles()!!.forEach { file ->
                val targetFile = File("${target.absolutePath}/${file.name}")
                if (file.isDirectory) copyDirectory(file, targetFile)
                else file.inputStream().use { inputStream ->
                    targetFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }

        /**
         * Caches file from [uri] and returns cached [File].
         * @param uri [Uri] to cache.
         * @param parentName Name of the parent folder in cache dir.
         */
        fun cacheFile(
            uri: Uri,
            parentName: String?,
            context: Context
        ): File? {
            return try {
                val isHTTP = uri.scheme == "http" || uri.scheme == "https"
                val inputStream = (
                        if (isHTTP) URL(uri.toString()).openStream()
                        else context.contentResolver.openInputStream(uri)
                        ) ?: return null
                val fileName = (
                        if (isHTTP) URLUtil.guessFileName(uri.toString(), null, null)
                        else getFileName(uri, context)
                        ) ?: "unknown"
                val file = writeToCache(
                    fileName = fileName,
                    inputStream = inputStream,
                    parentName = parentName,
                    context = context
                )
                inputStream.close()
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        private fun writeToCache(
            fileName: String,
            inputStream: InputStream,
            parentName: String?,
            context: Context
        ): File {
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            val outputFile = File("${cacheDir.absolutePath}${
                if (parentName != null) "/$parentName" else ""
            }/$fileName")
            outputFile.parentFile?.mkdirs()
            if (outputFile.exists()) outputFile.delete()
            val output = outputFile.outputStream()
            inputStream.copyTo(output)
            output.close()
            return outputFile
        }

        @SuppressLint("Range")
        private fun getFileName(uri: Uri, context: Context): String {
            var fileName: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor?.moveToFirst() == true) fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                cursor?.close()
            }
            if (fileName == null) {
                fileName = uri.path
                val cut = fileName!!.lastIndexOf("/")
                if (cut != -1) fileName = fileName.substring(cut + 1)
            }
            return fileName
        }
    }
}