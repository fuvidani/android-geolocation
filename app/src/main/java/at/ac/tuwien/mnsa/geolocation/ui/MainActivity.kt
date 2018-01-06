package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import at.ac.tuwien.mnsa.geolocation.GeoLocationApp
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.di.ApplicationComponent
import at.ac.tuwien.mnsa.geolocation.dto.Report
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.kotlin.where
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var applicationComponent: ApplicationComponent
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationComponent = (application as GeoLocationApp).getApplicationComponent()
        realm = Realm.getInstance(Utils.getNormalRealmConfig())
        setUpViewFragment()
        disposables.add(
                realm.where<Report>()
                        .findAllAsync()
                        .asFlowable()
                        .filter({ r -> r.isLoaded })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ results -> Timber.e("Results here. Size: ${results.size}") },
                                { e -> Timber.e("Error:$e") },
                                { Timber.e("Completed") }))
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

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        realm.close()
    }
}
