package io.github.aliernfrog.pftool_shared.util.staticutil

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.format.DateUtils
import android.webkit.URLUtil
import androidx.core.net.toUri
import io.github.aliernfrog.pftool_shared.data.Language
import io.github.aliernfrog.pftool_shared.util.extension.toPath
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.Locale
import kotlin.collections.indexOf

val hasAndroidDataRestrictions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

class PFToolSharedUtil {
    companion object {
        private const val DOCUMENTS_UI_PACKAGE = "com.android.documentsui"
        private const val GOOGLE_DOCUMENTS_UI_PACKAGE = "com.google.android.documentsui"

        fun documentsUIRestrictsAndroidData(context: Context): Boolean {
            if (!hasAndroidDataRestrictions) return false
            val documentsUIPackage = getDocumentsUIPackage(context)
            return documentsUIPackage?.let {
                it.longVersionCode >= when (it.packageName) {
                    DOCUMENTS_UI_PACKAGE -> 14
                    GOOGLE_DOCUMENTS_UI_PACKAGE -> 340916000
                    else -> Long.MAX_VALUE
                }
            } ?: false
        }

        fun getDocumentsUIPackage(context: Context) = try {
            context.packageManager.getPackageInfo(GOOGLE_DOCUMENTS_UI_PACKAGE, 0)
        } catch (_: Exception) {
            try {
                context.packageManager.getPackageInfo(DOCUMENTS_UI_PACKAGE, 0)
            } catch (_: Exception) {
                null
            }
        }

        fun getAppVersionName(context: Context): String {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName.toString()
        }

        fun getAppVersionCode(context: Context): Long {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        }

        /**
         * Gets [Language] from given language code.
         * [code] must either be a language code, or language and country code splitted by a "-" (e.g.: en-US, en)
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