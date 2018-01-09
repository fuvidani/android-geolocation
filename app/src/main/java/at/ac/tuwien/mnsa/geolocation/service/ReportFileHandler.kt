package at.ac.tuwien.mnsa.geolocation.service

import android.content.Context
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement
import at.ac.tuwien.mnsa.geolocation.dto.Report
import com.jakewharton.fliptables.FlipTableConverters
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class ReportFileHandler {

    fun getFile(context: Context, report: Report): Single<File> {
        return Single.create<File> { emitter ->
            val reportAsBytes = formatReport(context, report).toByteArray()
            try {
                val file = getTempFile(context, report.timestamp)
                val outputStream = FileOutputStream(file)
                outputStream.write(reportAsBytes, 0, reportAsBytes.size)
                outputStream.flush()
                emitter.onSuccess(file)
            } catch (e: IOException) {
                Timber.e(e)
                emitter.onError(e)
            }
        }
    }

    fun deleteReportFileIfAvailable(context: Context, report: Report): Single<Boolean> {
        return Single.create { emitter ->
            val file = getTempFile(context, report.timestamp)
            if (file.delete()) {
                emitter.onSuccess(true)
            } else {
                emitter.onError(IllegalArgumentException("File could not be deleted"))
            }
        }
    }

    private fun getTempFile(context: Context, reportId: Long): File {
        val directory = context.externalCacheDir
        return File("${directory.absolutePath}/geolocation_report_$reportId.txt")
    }

    private fun formatReport(context: Context, report: Report): String {
        val title = String.format(context.getString(R.string.txt_title), Utils.toReadableFormat(report.timestamp))
        val gpsLocation = String.format(context.getString(R.string.txt_gps_loc), report.actualLatitude, report.actualLongitude, Utils.roundDouble(report.gspAccuracy))
        val apLocation = String.format(context.getString(R.string.txt_meas_loc), report.assumedLatitude, report.assumedLongitude, Utils.roundDouble(report.assumedAccuracy))
        val difference = String.format(context.getString(R.string.txt_diff), Utils.roundDouble(report.positionDifference))
        return StringBuilder()
                .append(title)
                .append("\n\n")
                .append(gpsLocation)
                .append("\n")
                .append(apLocation)
                .append("\n")
                .append(difference)
                .append("\n\n")
                .append(getFormattedCellTowersTable(context, report))
                .append("\n\n")
                .append(getFormattedAccessPointsTable(context, report))
                .toString()

    }

    private fun getFormattedCellTowersTable(context: Context, report: Report): String {
        val title = context.getString(R.string.cell_towers_tv)
        val data = FlipTableConverters.fromIterable(report.towerMeasurements, CellTowerMeasurement::class.java)
        return StringBuilder()
                .append(title)
                .append("\n")
                .append(data)
                .toString()
    }

    private fun getFormattedAccessPointsTable(context: Context, report: Report): String {
        val title = context.getString(R.string.access_points_tv)
        val data = FlipTableConverters.fromIterable(report.pointMeasurements, AccessPointMeasurement::class.java)
        return StringBuilder()
                .append(title)
                .append("\n")
                .append(data)
                .toString()
    }
}