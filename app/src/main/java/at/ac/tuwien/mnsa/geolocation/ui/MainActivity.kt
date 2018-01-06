package at.ac.tuwien.mnsa.geolocation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import at.ac.tuwien.mnsa.geolocation.GeoLocationApp
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.di.ApplicationComponent
import at.ac.tuwien.mnsa.geolocation.dto.ReportDetailClickEvent
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var applicationComponent: ApplicationComponent
    private var alertDialog: AlertDialog? = null
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationComponent = (application as GeoLocationApp).getApplicationComponent()
        realm = Realm.getInstance(Utils.getNormalRealmConfig())
        EventBus.getDefault().register(this)
        setUpViewFragment()
    }

    private fun setUpViewFragment() {
        val mainFragment: MainFragment? = supportFragmentManager.findFragmentById(R.id.contentFrame) as MainFragment?
        if (mainFragment == null) {
            Utils.replaceFragmentInActivity(supportFragmentManager, MainFragment.newInstance(), R.id.contentFrame)
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Utils.fineLocationAccessCode())
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        if (permissions.isNotEmpty() && requestCode == Utils.fineLocationAccessCode() &&
                (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    Utils.coarseLocationAccessCode())
        }
        if (permissions.isNotEmpty() && requestCode == Utils.coarseLocationAccessCode()
                && (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            displayRequiredPermissionsDialog()
        }
    }

    fun getComponent(): ApplicationComponent {
        return applicationComponent
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(reportClick: ReportDetailClickEvent) {
        Timber.d("Click on report (ID: ${reportClick.reportId}")
    }

    private fun displayRequiredPermissionsDialog() {
        alertDialog = AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.dialog_required_permission_title))
                .setMessage(getString(R.string.dialog_required_permission_message))
                .setNeutralButton(getString(R.string.dialog_required_permission_ok), { _, _ -> checkPermissions() })
                .setCancelable(false)
                .create()
        alertDialog?.show()
    }

    override fun onStop() {
        super.onStop()
        alertDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        EventBus.getDefault().unregister(this)
    }
}
