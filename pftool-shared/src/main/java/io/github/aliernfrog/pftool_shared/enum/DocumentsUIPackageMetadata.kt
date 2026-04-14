package io.github.aliernfrog.pftool_shared.enum

enum class DocumentsUIPackageMetadata(
    val packageName: String,
    val androidDataRestrictedVersion: Long
) {
    AOSP(
        packageName = "com.android.documentsui",
        androidDataRestrictedVersion = 14
    ),

    GOOGLE(
        packageName = "com.google.android.documentsui",
        androidDataRestrictedVersion = 340916000
    )
}