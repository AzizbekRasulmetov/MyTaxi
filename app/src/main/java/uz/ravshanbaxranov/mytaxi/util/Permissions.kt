package uz.ravshanbaxranov.mytaxi.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions
import uz.ravshanbaxranov.mytaxi.util.Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import uz.ravshanbaxranov.mytaxi.util.Constants.PERMISSION_LOCATION_REQUEST_CODE

object Permissions {


    fun checkLocationAndNotificationPermissions(context: Context, fragment: Fragment) {
        if (hasLocationAndNotificationPermission(context)) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.requestPermissions(
                fragment,
                "You need to accept location permissions to use this app.",
                PERMISSION_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            EasyPermissions.requestPermissions(
                fragment,
                "You need to accept location permissions to use this app.",
                PERMISSION_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }


    fun hasLocationAndNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }


    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else true
    }


    @SuppressLint("InlinedApi")
    fun checkBackgroundLocationPermission(context: Context, fragment: Fragment) {
        if (hasBackgroundLocationPermission(context)) {
            return
        } else {
            EasyPermissions.requestPermissions(
                fragment,
                "This application cannot work without permission",
                PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }



}



