package at.ac.tuwien.mnsa.geolocation.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import at.ac.tuwien.mnsa.geolocation.dto.CellTower
import timber.log.Timber

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class CellsInformationRetriever(
        private val context: Context) {

    fun getCellsInformation(): List<CellTower> {
        if (permissionGranted()) {
            val telephonyService = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val cellInfoList: List<CellInfo>? = telephonyService.allCellInfo
            return if (cellInfoList == null || cellInfoList.isEmpty()) {
                Timber.e("AllCellInfo returned null or empty list")
                ArrayList()
            } else {
                cellInfoList.map { cellInfo -> CellInfoParser.parseCellInfo(cellInfo) }.toList()
            }
        }
        Timber.e("No GPS permission granted")
        return ArrayList()
    }

    private fun permissionGranted(): Boolean = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

}