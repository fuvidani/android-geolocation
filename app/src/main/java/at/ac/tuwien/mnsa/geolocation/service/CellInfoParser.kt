package at.ac.tuwien.mnsa.geolocation.service

import android.telephony.*
import at.ac.tuwien.mnsa.geolocation.dto.CellTower
import at.ac.tuwien.mnsa.geolocation.dto.CellType

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class CellInfoParser private constructor() {

    companion object {
        fun parseCellInfo(cellInfo: CellInfo): CellTower {
            when (cellInfo) {
                is CellInfoCdma -> {
                    return fromCdma(cellInfo)
                }
                is CellInfoGsm -> {
                    return fromGsm(cellInfo)
                }
                is CellInfoLte -> {
                    return fromLte(cellInfo)
                }
                is CellInfoWcdma -> {
                    return fromWcdma(cellInfo)
                }
                else -> {
                    return CellTower(System.currentTimeMillis().toString())
                }
            }
        }

        private fun fromCdma(cellInfo: CellInfoCdma): CellTower {
            val cellIdentity: CellIdentityCdma = cellInfo.cellIdentity
            val signalStrength: CellSignalStrength = cellInfo.cellSignalStrength
            val tower = CellTower(toRangedString(cellIdentity.basestationId),
                    toRangedString(cellIdentity.networkId),
                    signalStrength.dbm,
                    signalStrength.level,
                    signalStrength.asuLevel)
            tower.cellType = CellType.CDMA
            return tower
        }

        private fun fromGsm(cellInfo: CellInfoGsm): CellTower {
            val cellIdentity: CellIdentityGsm = cellInfo.cellIdentity
            val signalStrength: CellSignalStrength = cellInfo.cellSignalStrength
            val tower = CellTower(toRangedString(cellIdentity.cid),
                    toRangedString(cellIdentity.mcc),
                    toRangedString(cellIdentity.mnc),
                    toRangedString(cellIdentity.lac),
                    signalStrength.dbm,
                    signalStrength.level,
                    signalStrength.asuLevel)
            tower.cellType = CellType.GSM
            return tower
        }

        private fun fromLte(cellInfo: CellInfoLte): CellTower {
            val cellIdentity: CellIdentityLte = cellInfo.cellIdentity
            val signalStrength: CellSignalStrength = cellInfo.cellSignalStrength
            val tower = CellTower(toRangedString(cellIdentity.ci),
                    toRangedString(cellIdentity.mcc),
                    toRangedString(cellIdentity.mnc),
                    toRangedString(cellIdentity.tac),
                    signalStrength.dbm,
                    signalStrength.level,
                    signalStrength.asuLevel)
            tower.cellType = CellType.LTE
            return tower
        }

        private fun fromWcdma(cellInfo: CellInfoWcdma): CellTower {
            val cellIdentity: CellIdentityWcdma = cellInfo.cellIdentity
            val signalStrength: CellSignalStrength = cellInfo.cellSignalStrength
            val tower = CellTower(toRangedString(cellIdentity.cid),
                    toRangedString(cellIdentity.mcc),
                    toRangedString(cellIdentity.mnc),
                    toRangedString(cellIdentity.lac),
                    signalStrength.dbm,
                    signalStrength.level,
                    signalStrength.asuLevel)
            tower.cellType = CellType.WCDMA
            return tower
        }

        private fun toRangedString(aInt: Int): String {
            return if (aInt == Int.MAX_VALUE) {
                "N/A"
            } else {
                aInt.toString()
            }
        }
    }
}