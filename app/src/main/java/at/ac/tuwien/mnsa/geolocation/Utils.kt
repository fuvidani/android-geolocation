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

        fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int, addToBackStack: Boolean, animation: Boolean) {
            val transaction = fragmentManager.beginTransaction()
            if (animation) {
                transaction.setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out,
                        android.R.animator.fade_in,
                        android.R.animator.fade_out)
            }
            if (addToBackStack) {
                transaction.addToBackStack(null)
            }
            transaction.replace(frameId, fragment)
            transaction.commit()
        }

        fun fineLocationAccessCode(): Int {
            return 42
        }

        fun coarseLocationAccessCode(): Int {
            return 43
        }

        fun getStreetViewUrl(width: Int, height: Int, lat: Double, lon: Double): String {
            return "https://maps.googleapis.com/maps/api/streetview?size=${width}x$height&location=$lat,$lon&fov=90"
        }
    }
}