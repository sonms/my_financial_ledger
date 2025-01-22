package com.purang.financial_ledger.screen.setting

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.purang.financial_ledger.preferences_data_store.PreferencesDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val darkMode by PreferencesDataStore.getState(context).collectAsState(initial = false)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        SettingItem(
            icon = Icons.Default.Info,
            title = "다크 모드 설정",
            description = "Enable dark mode for the app",
            switchState = darkMode,
            onSwitchChange = { state ->
                scope.launch {
                    PreferencesDataStore.saveState(context, state)
                    Log.e("stateSet", "$state")
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    switchState: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = { onClick?.invoke() })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description, style = MaterialTheme.typography.bodyMedium)
        }

        if (switchState != null && onSwitchChange != null) {
            Switch(
                checked = switchState,
                onCheckedChange = { onSwitchChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color.Gray,
                    checkedThumbColor = Color.LightGray,
                    checkedBorderColor = Color.DarkGray,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedThumbColor = Color.DarkGray,
                )
            )
        }
    }
}