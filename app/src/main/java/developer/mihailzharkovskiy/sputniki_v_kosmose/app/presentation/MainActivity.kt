package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.dialog_user_location.DialogUserLocation
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userlocation: UserLocationSource

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        binding.mainNavBottom.setupWithNavController(navHost.navController)
        binding.mainNavBottom.itemIconTintList =
            null //нужно чтобы нормально отображалась png иконка

        requestPermission()
    }


    private fun requestPermission() {
        when (userlocation.checkPermission()) {
            is PermissionState.NoPermission -> checkFirstRun()
            is PermissionState.YesPermission -> userlocation.updateUserLocation()
        }
    }

    private fun checkFirstRun() {
        val KEY_FIRST_LAUNCH = "KEY_FIRST_LAUNCH"
        val SHARED_PREF = "HARED_PREF"
        val preferences = this.getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        if (preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
            DialogUserLocation.show(supportFragmentManager)
        }
    }
}
