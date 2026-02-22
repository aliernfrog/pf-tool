package io.github.aliernfrog.shared.ui.sheet

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import io.github.aliernfrog.shared.data.Social
import io.github.aliernfrog.shared.ui.component.AppModalBottomSheet
import io.github.aliernfrog.shared.ui.component.ButtonIcon
import io.github.aliernfrog.shared.ui.component.ErrorWithIcon
import io.github.aliernfrog.shared.ui.component.crash_handler.CrashDetails
import io.github.aliernfrog.shared.util.SharedString
import io.github.aliernfrog.shared.util.TAG
import io.github.aliernfrog.shared.util.sharedStringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CrashDetailsSheet(
    throwable: Throwable?,
    crashReportURL: String,
    debugInfo: String,
    supportLinks: List<Social>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var stackTrace by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(throwable) {
        try {
            if (throwable == null) sheetState.hide()
            else {
                stackTrace = throwable.stackTraceToString()
                sheetState.show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "CrashDetailsSheet/LaunchedEffect: Failed to update sheet state", e)
        }
    }

    AppModalBottomSheet(
        sheetState = sheetState
    ) {
        ErrorWithIcon(
            title = sharedStringResource(SharedString::warningError),
            icon = rememberVectorPainter(Icons.Rounded.PriorityHigh),
            iconContainerShape = MaterialShapes.Triangle.toShape(),
            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
        )

        CrashDetails(
            crashReportURL = crashReportURL,
            stackTrace = stackTrace,
            debugInfo = "$debugInfo\nThis is a soft crash",
            supportLinks = supportLinks,
            secondaryOption = {
                OutlinedButton(
                    onClick = { scope.launch {
                        sheetState.hide()
                    } },
                    shapes = ButtonDefaults.shapes()
                ) {
                    ButtonIcon(rememberVectorPainter(Icons.Default.Close))
                    Text(sharedStringResource(SharedString::actionDismiss))
                }
            }
        )
    }
}