package at.ac.tuwien.mnsa.geolocation


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import io.realm.RealmConfiguration

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class Utils {

    companion object {
        fun getNormalRealmConfig(): RealmConfiguration {
            return RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        }

        fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(frameId, fragment)
            transaction.commit()
        }
    }
}