package at.ac.tuwien.mnsa.geolocation.di

import at.ac.tuwien.mnsa.geolocation.ui.MainActivity
import dagger.Component
import dagger.Module
import javax.inject.Singleton

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
@Singleton
@Component(modules = [(AppModule::class)])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
}

@Module
open class AppModule