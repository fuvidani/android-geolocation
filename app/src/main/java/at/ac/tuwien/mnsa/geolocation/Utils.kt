package at.ac.tuwien.mnsa.geolocation


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.format.DateFormat
import io.realm.RealmConfiguration
import java.math.BigDecimal
import java.util.*

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
        // TODO remove this method -> inject realm object by using persistence module
        fun getNormalRealmConfig(): RealmConfiguration {
            return RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        }

        fun replaceFragmentInActivity(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int, addToBackStack: Boolean, animation: Boolean) {
            val transaction = fragmentManager.beginTransaction()
            if (animation) {
                transaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
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

        fun getStreetViewUrl(width: Int, height: Int, gpsLat: Double, gpsLon: Double, wifiLat: Double, wifiLon: Double): String {
            return "https://maps.googleapis.com/maps/api/staticmap?center=$gpsLat,$gpsLon&zoom=18&size=${width}x$height&markers=label:G%7C$gpsLat,$gpsLon&markers=color:blue%7Clabel:W%7C$wifiLat,$wifiLon"
        }

        fun toReadableFormat(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd. MMMM yyyy HH:mm:ss", cal).toString()
        }

        fun roundDouble(number: Double): Double {
            return BigDecimal(number).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        }
    }
}