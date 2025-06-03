package com.armandodarienzo.composecleanpermissions.ui.base

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.net.Uri
import android.provider.Settings


fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.goToPermissionSettings() {
    var context = this
    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.setData(uri)
    context.startActivity(intent)
}

/**
 * Extension function on Context to get a list of unique, localized display names
 * for a list of permission manifest strings, prioritizing permission group names.
 *
 * This helps present permissions similarly to how they are grouped in OS settings.
 *
 * @param permissionNames The list of permission manifest strings
 * (e.g., listOf("android.permission.CAMERA", "android.permission.READ_CONTACTS")).
 * @return A list of unique localized display names (group names or individual permission names),
 * or "Unknown Permission" for any permission name not found.
 */
fun Context.getLocalizedPermissionDisplayNames(permissionNames: List<String>): List<String> {
    val displayNames = mutableSetOf<String>()
    val packageManager = this.packageManager

    for (permissionName in permissionNames) {
        try {
            val permissionInfo: PermissionInfo = packageManager.getPermissionInfo(permissionName, 0)

            if (permissionInfo.group != null) {
                // Permission belongs to a group, try to get the group's label
                try {
                    val permissionGroupInfo: PermissionGroupInfo = packageManager.getPermissionGroupInfo(permissionInfo.name, 0)
                    displayNames.add(permissionGroupInfo.loadLabel(packageManager).toString())
                } catch (e: PackageManager.NameNotFoundException) {
                    // Group name not found, fall back to individual permission label
                    displayNames.add(permissionInfo.loadLabel(packageManager).toString())
                }
            } else {
                // Permission does not belong to a group, use its own label
                displayNames.add(permissionInfo.loadLabel(packageManager).toString())
            }

        } catch (e: PackageManager.NameNotFoundException) {
            // Permission name not found on the system
            displayNames.add("Unknown Permission: $permissionName") // Indicate which permission was not found
        } catch (e: Exception) {
            // Handle other potential exceptions
            e.printStackTrace()
            displayNames.add("Error retrieving permission name for $permissionName")
        }
    }

    return displayNames.toList()
}