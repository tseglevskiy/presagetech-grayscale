package com.jollydroid.imageutildemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WithPermissions(
    modifier: Modifier = Modifier,
    permissions: List<String>,
    initialMessage: String = "This permission is needed to continue. Please grant access.",
    rationaleMessage: String = "We need this permission to provide full functionality. You can grant it by tapping below",
    content: @Composable (modifier: Modifier) -> Unit
) {
    val permissionsState = rememberMultiplePermissionsState(permissions)

    when {
        permissionsState.allPermissionsGranted -> {
            content(modifier)
        }

        permissionsState.shouldShowRationale -> {
            Column(modifier.padding(16.dp)) {
                Text(rationaleMessage)
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Allow")
                }
            }
        }

        else -> {
            Column(modifier.padding(16.dp)) {
                Text(initialMessage)
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Allow")
                }
            }
        }
    }
}