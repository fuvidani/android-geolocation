package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
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

    fun getComponent(): ApplicationComponent {
        return applicationComponent
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(reportClick: ReportDetailClickEvent) {
        Timber.d("Click on report (ID: ${reportClick.reportId}")
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        EventBus.getDefault().unregister(this)
    }
}
