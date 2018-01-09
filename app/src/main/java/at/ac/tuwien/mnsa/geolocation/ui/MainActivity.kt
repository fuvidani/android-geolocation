package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.ReportDetailClickEvent
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realm = Realm.getInstance(Utils.getNormalRealmConfig())
        EventBus.getDefault().register(this)
        setUpViewFragment()
    }

    private fun setUpViewFragment() {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (fragment == null) {
            Utils.replaceFragmentInActivity(supportFragmentManager, MainFragment.newInstance(), R.id.contentFrame, false, false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(reportClick: ReportDetailClickEvent) {
        val bundle = Bundle()
        bundle.putLong("reportId", reportClick.reportId)
        val fragment = DetailViewFragment.newInstance()
        fragment.arguments = bundle
        Utils.replaceFragmentInActivity(supportFragmentManager, fragment, R.id.contentFrame, true, true)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
        EventBus.getDefault().unregister(this)
    }
}
