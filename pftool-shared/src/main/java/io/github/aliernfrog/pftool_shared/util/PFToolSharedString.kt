package io.github.aliernfrog.pftool_shared.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.aliernfrog.shared.di.getKoinInstance
import org.koin.compose.koinInject
import kotlin.reflect.KProperty1

data class PFToolSharedString(
    @StringRes val actionMore : Int,
    @StringRes val actionSelectDeselectAll : Int,
    @StringRes val actionSelectSelectAll : Int,
    @StringRes val actionTapToDismiss : Int,

    @StringRes val info : Int,
    @StringRes val infoShizukuDisconnected : Int,

    @StringRes val listOptions : Int,
    @StringRes val listSorting : Int,
    @StringRes val listSortingName : Int,
    @StringRes val listSortingDate : Int,
    @StringRes val listSortingSize : Int,
    @StringRes val listSortingReversed : Int,
    @StringRes val listStyle : Int,
    @StringRes val listStyleList : Int,
    @StringRes val listStyleGrid : Int,
    @StringRes val listStyleGridMaxLineSpan : Int,

    @StringRes val maps : Int,
    @StringRes val mapsMapName : Int,
    @StringRes val mapsMapNameGuide : Int,
    @StringRes val mapsThumbnail : Int,
    @StringRes val mapsRename : Int,
    @StringRes val mapsDuplicate : Int,
    @StringRes val mapsListReload : Int,
    @StringRes val mapsListStorage : Int,
    @StringRes val mapsListSearch : Int,
    @StringRes val mapsListSearchClear : Int,
    @StringRes val mapsListSearchNoMatches : Int,
    @StringRes val mapsListCount : Int,
    @StringRes val mapsListMultiSelection : Int,
    @StringRes val mapsListPickMapFailed : Int,
    @StringRes val mapsListSegmentImported : Int,
    @StringRes val mapsListSegmentImportedNoMaps: Int,
    @StringRes val mapsListSegmentExported : Int,
    @StringRes val mapsListSegmentExportedNoMaps : Int,
    @StringRes val mapsListSegmentShared : Int,
    @StringRes val mapsListSegmentSharedNoMaps : Int,

    @StringRes val permissionsDowngradeFilesApp : Int,
    @StringRes val permissionsDowngradeFilesAppGuide : Int,
    @StringRes val permissionsDowngradeFilesAppDescription : Int,
    @StringRes val permissionsDowngradeFilesAppUninstall : Int,
    @StringRes val permissionsDowngradeFilesAppCant : Int,
    @StringRes val permissionsChooseFolder : Int,
    @StringRes val permissionsOther : Int,
    @StringRes val permissionsSAFFoldersNeeded : Int,
    @StringRes val permissionsSAFAllFiles : Int,
    @StringRes val permissionsSAFAllFilesDescription : Int,
    @StringRes val permissionsShizukuIntroduction : Int,
    @StringRes val permissionsShizukuInstallTitle : Int,
    @StringRes val permissionsShizukuNotRunning : Int,
    @StringRes val permissionsShizukuNotRunningDescription : Int,
    @StringRes val permissionsShizukuPermission : Int,
    @StringRes val permissionsShizukuPermissionDescription : Int,
    @StringRes val permissionsShizukuPermissionGrant : Int,
    @StringRes val permissionsShizukuOpenShizuku : Int,
    @StringRes val permissionsShizukuInstallShizuku : Int,
    @StringRes val permissionsShizukuRooted : Int,
    @StringRes val permissionsShizukuRootedDescription : Int,
    @StringRes val permissionsShizukuSui : Int,
    @StringRes val permissionsShizukuWaitingService : Int,
    @StringRes val permissionsShizukuWaitingServiceTimedOut : Int,
    @StringRes val permissionsShizukuWaitingServiceTimedOutRestart : Int,
    @StringRes val permissionsShizukuProblematicVersion : Int,
    @StringRes val permissionsShizukuProblematicVersionNote : Int,
    @StringRes val permissionsShizukuProblematicVersionDownloadRecommended : Int,
    @StringRes val permissionsShizukuProblematicVersionAllVersions : Int,
    @StringRes val permissionsAllFilesTitle : Int,
    @StringRes val permissionsAllFilesDescription : Int,
    @StringRes val permissionsAllFilesGrant : Int,
    @StringRes val permissionsAllFilesDenied : Int,
    @StringRes val permissionsAllFilesSAF : Int,
    @StringRes val permissionsAllFilesSAFDescription : Int,
    @StringRes val permissionsRecommendedFolder : Int,
    @StringRes val permissionsRecommendedFolderA7Hint : Int,
    @StringRes val permissionsRecommendedFolderA8Hint : Int,
    @StringRes val permissionsNotRecommendedFolderDescription : Int,
    @StringRes val permissionsNotRecommendedFolderUseAnyway : Int,
    @StringRes val permissionsNotRecommendedFolderChooseRecommendedFolder : Int,
    @StringRes val permissionsNotRecommendedFolderAdvancedHide : Int,
    @StringRes val permissionsNotRecommendedFolderAdvancedShow : Int,
    @StringRes val permissionsSetupShizuku : Int,
    @StringRes val permissionsSetupShizukuDescription : Int,

    @StringRes val settingsLanguage : Int,
    @StringRes val settingsLanguageSystem : Int,
    @StringRes val settingsLanguageSystemFollow : Int,
    @StringRes val settingsLanguageSystemNotAvailable : Int,
    @StringRes val settingsLanguageOther : Int,
    @StringRes val settingsLanguageProgressBase : Int,
    @StringRes val settingsLanguageProgressPercent : Int,
    @StringRes val settingsLanguageHelp : Int,
    @StringRes val settingsLanguageHelpDescription : Int,
    @StringRes val settingsLanguageHelpDeviceNotAvailable : Int,
    @StringRes val settingsMaps : Int,
    @StringRes val settingsMapsThumbnails : Int,
    @StringRes val settingsMapsThumbnailsChosen : Int,
    @StringRes val settingsMapsThumbnailsChosenDescription : Int,
    @StringRes val settingsMapsThumbnailsList : Int,
    @StringRes val settingsMapsThumbnailsListDescription : Int,
    @StringRes val settingsMapsBehavior : Int,
    @StringRes val settingsMapsBehaviorStackup : Int,
    @StringRes val settingsMapsBehaviorStackupDescription : Int,
    @StringRes val settingsStorage : Int,
    @StringRes val settingsStorageStorageAccessType : Int,
    @StringRes val settingsStorageStorageAccessTypeSAF : Int,
    @StringRes val settingsStorageStorageAccessTypeSAFDescription : Int,
    @StringRes val settingsStorageStorageAccessTypeShizuku : Int,
    @StringRes val settingsStorageStorageAccessTypeShizukuDescription : Int,
    @StringRes val settingsStorageStorageAccessTypeAllFiles : Int,
    @StringRes val settingsStorageStorageAccessTypeAllFilesDescription : Int,
    @StringRes val settingsStorageFolders : Int,
    @StringRes val settingsStorageFoldersChoose : Int,
    @StringRes val settingsStorageFoldersRestoreDefault : Int,
    @StringRes val settingsStorageFoldersRecommendedFolder : Int,
    @StringRes val settingsStorageFoldersNotSet : Int,
    @StringRes val settingsStorageFoldersOpenCurrent : Int,
    @StringRes val settingsStorageFoldersOpenRecommended : Int,
    @StringRes val settingsStorageFoldersOpenAndroidData : Int,
)

@Composable
fun sharedStringResource(property: KProperty1<PFToolSharedString, Int>): String {
    val provider = koinInject<PFToolSharedString>()
    return stringResource(property.get(provider))
}

fun Context.getSharedString(property: KProperty1<PFToolSharedString, Int>): String {
    val provider = getKoinInstance<PFToolSharedString>()
    return getString(property.get(provider))
}