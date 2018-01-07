package at.ac.tuwien.mnsa.geolocation


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.format.DateFormat
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement
import at.ac.tuwien.mnsa.geolocation.dto.Report
import io.realm.RealmConfiguration
import io.realm.RealmList
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

        fun getStreetViewUrl(width: Int, height: Int, lat: Double, lon: Double): String {
            return "https://maps.googleapis.com/maps/api/streetview?size=${width}x$height&location=$lat,$lon&fov=90"
        }

        fun toReadableFormat(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd. MMMM yyyy HH:mm:ss", cal).toString()
        }

        fun generateDummyReport(): Report {
            val towerMeasurements = RealmList<CellTowerMeasurement>()
            for (a in 1..10) {
                towerMeasurements.add(generateDummyTowerMeasurement())
            }
            val accessPointsMeasurements = RealmList<AccessPointMeasurement>()
            for (a in 1..10) {
                accessPointsMeasurements.add(generateDummyAccessPointMeasurement())
            }
            return Report(System.currentTimeMillis(), 36.112764, -113.9960696, 350.0, 36.112764, -113.9960696, 5.0, 100.0, towerMeasurements, accessPointsMeasurements)
        }

        private fun generateDummyTowerMeasurement(): CellTowerMeasurement {
            return CellTowerMeasurement("666666", "77777", "88888", "123456")
        }

        private fun generateDummyAccessPointMeasurement(): AccessPointMeasurement {
            return AccessPointMeasurement("00:80:41:ae:fd:7e", 100, 100)
        }
    }
}