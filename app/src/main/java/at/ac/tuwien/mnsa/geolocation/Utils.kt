package at.ac.tuwien.mnsa.geolocation

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
    }
}