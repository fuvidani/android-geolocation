package at.ac.tuwien.mnsa.geolocation

import android.app.Application
import at.ac.tuwien.mnsa.geolocation.di.AppModule
import at.ac.tuwien.mnsa.geolocation.di.ApplicationComponent
import at.ac.tuwien.mnsa.geolocation.di.DaggerApplicationComponent
import io.realm.Realm

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class GeoLocationApp : Application() {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        applicationComponent = DaggerApplicationComponent.builder().appModule(AppModule()).build()
    }

    fun getApplicationComponent(): ApplicationComponent {
        return applicationComponent
    }
}