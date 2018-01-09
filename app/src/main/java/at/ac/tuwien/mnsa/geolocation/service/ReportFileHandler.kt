package at.ac.tuwien.mnsa.geolocation.service

import android.content.Context
import at.ac.tuwien.mnsa.geolocation.dto.Report
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
            val reportAsBytes = formatReport(report).toByteArray()
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

    private fun formatReport(report: Report): String {
        return "Nice report"
    }
}