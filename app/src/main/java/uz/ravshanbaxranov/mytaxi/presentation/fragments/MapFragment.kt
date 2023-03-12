package uz.ravshanbaxranov.mytaxi.presentation.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import uz.ravshanbaxranov.mytaxi.R
import uz.ravshanbaxranov.mytaxi.data.model.Track
import uz.ravshanbaxranov.mytaxi.databinding.FragmentMapBinding
import uz.ravshanbaxranov.mytaxi.presentation.viewmodel.MapViewModel
import uz.ravshanbaxranov.mytaxi.service.TrackerService
import uz.ravshanbaxranov.mytaxi.util.Constants.ACTION_SERVICE_START
import uz.ravshanbaxranov.mytaxi.util.Constants.ACTION_SERVICE_STOP
import uz.ravshanbaxranov.mytaxi.util.Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import uz.ravshanbaxranov.mytaxi.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import uz.ravshanbaxranov.mytaxi.util.Permissions.checkBackgroundLocationPermission
import uz.ravshanbaxranov.mytaxi.util.Permissions.checkLocationAndNotificationPermissions
import uz.ravshanbaxranov.mytaxi.util.Permissions.hasBackgroundLocationPermission
import uz.ravshanbaxranov.mytaxi.util.Permissions.hasLocationAndNotificationPermission
import uz.ravshanbaxranov.mytaxi.util.isDarkTheme
import uz.ravshanbaxranov.mytaxi.util.showLog

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), EasyPermissions.PermissionCallbacks {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private val binding by viewBinding(FragmentMapBinding::bind)
    private val viewModel: MapViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mapView = binding.mapView
        mapboxMap = mapView.getMapboxMap()

        if (isDarkTheme(requireContext())) {
            mapView.getMapboxMap().loadStyleUri(Style.DARK)
        } else {
            mapView.getMapboxMap().loadStyleUri(Style.OUTDOORS)
        }

        lifecycleScope.launchWhenCreated {
            TrackerService.locationChannel.asSharedFlow().collect {
//                viewModel.addTrackToDatabase(
//                    Track(
//                        latitude = it.latitude(),
//                        longitude = it.longitude()
//                    )
//                )
            }
        }

        binding.myLocationBtn.setOnClickListener {
            animateToLocation()
        }

        binding.zoomOutBtn.setOnClickListener {
            val zoomInCameraOptions = CameraOptions.Builder()
                .zoom(mapboxMap.cameraState.zoom - 1)
                .build()

            mapboxMap.easeTo(zoomInCameraOptions)
        }

        binding.zoomInBtn.setOnClickListener {
            val zoomInCameraOptions = CameraOptions.Builder()
                .zoom(mapboxMap.cameraState.zoom + 1)
                .build()
            mapboxMap.easeTo(zoomInCameraOptions)
        }

        checkLocationAndNotificationPermissions(requireContext(), this)

        if (hasBackgroundLocationPermission(requireContext())) {
            checkGpsStatus()
        }
        if (hasLocationAndNotificationPermission(requireContext())) {
            checkBackgroundLocationPermission(requireContext(), this)
        }

        initialViews()


    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun initialViews() {


        mapView.apply {
            scalebar.enabled = false
            compass.enabled = false
            logo.enabled = false
            attribution.enabled = false
            location.updateSettings {
                enabled = true
                pulsingEnabled = false
                locationPuck = LocationPuck2D(
                    topImage = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_yellow_taxi
                    )
                )
            }
        }
        binding.notificationBtn.post {
            val badgeDrawable = BadgeDrawable.create(requireContext())
            badgeDrawable.number = 1
            BadgeUtils.attachBadgeDrawable(
                badgeDrawable,
                binding.notificationBtn
            )
        }
        binding.stateRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.busy_rb -> {
                    sendServiceAction(ACTION_SERVICE_STOP)
                    binding.busyRb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.freeRb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.solid_dark
                        )
                    )
                }
                R.id.free_rb -> {
                    sendServiceAction(ACTION_SERVICE_START)

                    binding.busyRb.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.solid_dark)
                    )
                    binding.freeRb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            }
        }
    }


    private fun sendServiceAction(action: String) {
        Intent(requireContext(), TrackerService::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            checkLocationAndNotificationPermissions(requireContext(), this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            checkBackgroundLocationPermission(requireContext(), this)
        }
        if (requestCode == PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE) {
            checkGpsStatus()
        }
    }

    private fun checkGpsStatus() {
        val manager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }


    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }


    @SuppressLint("MissingPermission")
    private fun animateToLocation() {
        val locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        locationEngine.getLastLocation(object : LocationEngineCallback<LocationEngineResult?> {
            override fun onSuccess(result: LocationEngineResult?) {
                val location: Location? = result?.lastLocation
                if (location != null) {
                    val cameraOptions = CameraOptions.Builder()
                        .zoom(15.0)
                        .center(Point.fromLngLat(location.longitude, location.latitude))
                        .build()

                    mapboxMap.easeTo(cameraOptions)
                }
            }

            override fun onFailure(exception: Exception) {
                showLog(exception.message.toString())
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}