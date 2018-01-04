package at.ac.tuwien.mnsa.geolocation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import at.ac.tuwien.mnsa.geolocation.di.ApplicationComponent

class MainActivity : AppCompatActivity() {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationComponent = (application as GeoLocationApp).getApplicationComponent()
    }

    fun getComponent(): ApplicationComponent {
        return applicationComponent
    }
}
