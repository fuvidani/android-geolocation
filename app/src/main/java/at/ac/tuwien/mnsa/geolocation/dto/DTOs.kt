package at.ac.tuwien.mnsa.geolocation.dto

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
data class CellTower(
        var cellId: String = "",
        var cellType: CellType = CellType.UNAVAILABLE,
        var countryCode: String = "unavailable",
        var networkId: String = "unavailable",
        var locationAreaCode: String = "unavailable",
        var signalStrengthAsDbm: Int = -1,
        var signalLevel: Int = -1,
        var signalLevelAsAsu: Int = -1) {

    constructor(cellId: String = "",
                countryCode: String = "unavailable",
                networkId: String = "unavailable",
                locationAreaCode: String = "unavailable",
                signalStrengthAsDbm: Int = -1,
                signalLevel: Int = -1,
                signalLevelAsAsu: Int = -1) : this(cellId, CellType.UNAVAILABLE, countryCode, networkId, locationAreaCode, signalStrengthAsDbm, signalLevel, signalLevelAsAsu)

    constructor(cellId: String = "",
                networkId: String = "unavailable",
                signalStrengthAsDbm: Int = -1,
                signalLevel: Int = -1,
                signalLevelAsAsu: Int = -1) : this(cellId, CellType.UNAVAILABLE, "unavailable", networkId, "unavailable", signalStrengthAsDbm, signalLevel, signalLevelAsAsu)

}

enum class CellType {
    CDMA,
    GSM,
    LTE,
    WCDMA,
    UNAVAILABLE
}

open class CellTowerMeasurement(
        var cellId: String = "",
        var countryCode: String = "",
        var networkId: String = "",
        var locationAreaCode: String = ""
) : RealmObject()

open class AccessPointMeasurement(
        var address: String = "",
        var strength: Int = -1,
        var channel: Int = -1
) : RealmObject()

open class Report(
        @PrimaryKey
        var timestamp: Long = -1L,
        var actualLatitude: Double = 0.0,
        var actualLongitude: Double = 0.0,
        var gspAccuracy: Double = 0.0,
        var assumedLatitude: Double = 0.0,
        var assumedLongitude: Double = 0.0,
        var assumedAccuracy: Double = 0.0,
        var accuracyDifference: Double = 0.0,
        var towerMeasurements: RealmList<CellTowerMeasurement> = RealmList(),
        var pointMeasurements: RealmList<AccessPointMeasurement> = RealmList()
) : RealmObject()

open class LocationServiceKey(
        var key: String = "test"
) : RealmObject()

data class ReportDetailClickEvent(val reportId: Long)

data class ReportDeleteClickEvent(val reportId: Long)

data class ReportGeneratedEvent(val reportId: Long)