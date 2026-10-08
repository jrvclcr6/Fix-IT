package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.SignalWifiBad
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    fun getIcon(name: String): ImageVector {
        return when (name) {
            "desktop_windows" -> Icons.Default.DesktopWindows
            "code" -> Icons.Default.Code
            "wifi" -> Icons.Default.Wifi
            "speed" -> Icons.Default.Speed
            "monitor" -> Icons.Default.Monitor
            "print" -> Icons.Default.Print
            "warning" -> Icons.Default.Warning
            "power_settings_new" -> Icons.Default.PowerSettingsNew
            "tv_off" -> Icons.Default.TvOff
            "thermostat" -> Icons.Default.Thermostat
            "wifi_off" -> Icons.Default.WifiOff
            "bug_report" -> Icons.Default.BugReport
            "equalizer" -> Icons.Default.Equalizer
            "water_drop" -> Icons.Default.WaterDrop
            "battery_alert" -> Icons.Default.BatteryAlert
            "storage" -> Icons.Default.Storage
            "restart_alt" -> Icons.Default.RestartAlt
            "apps" -> Icons.Default.Apps
            "signal_wifi_bad" -> Icons.Default.SignalWifiBad
            "settings_ethernet" -> Icons.Default.SettingsEthernet
            "hourglass_bottom" -> Icons.Default.HourglassBottom
            "broken_image" -> Icons.Default.BrokenImage
            "usb" -> Icons.Default.Usb
            "notifications_active" -> Icons.Default.NotificationsActive
            "volume_off" -> Icons.AutoMirrored.Filled.VolumeOff
            "bluetooth_disabled" -> Icons.Default.BluetoothDisabled
            "keyboard" -> Icons.Default.Keyboard
            "system_update_alt" -> Icons.Default.SystemUpdateAlt
            "air" -> Icons.Default.Air
            "password" -> Icons.Default.Password
            "security" -> Icons.Default.Security
            "wifi_find" -> Icons.Default.WifiOff
            "network_check" -> Icons.Default.NetworkCheck
            "data_usage" -> Icons.Default.DataUsage
            "sports_esports" -> Icons.Default.SportsEsports
            "aspect_ratio" -> Icons.Default.AspectRatio
            "mouse" -> Icons.Default.Mouse
            "videocam_off" -> Icons.Default.VideocamOff
            "schedule" -> Icons.Default.Schedule
            else -> Icons.AutoMirrored.Filled.HelpOutline
        }
    }
}
