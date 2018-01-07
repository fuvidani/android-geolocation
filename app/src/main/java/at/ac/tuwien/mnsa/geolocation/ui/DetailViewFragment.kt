package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import at.ac.tuwien.mnsa.geolocation.ui.recyclerviews.AccessPointsAdapter
import at.ac.tuwien.mnsa.geolocation.ui.recyclerviews.CellTowersAdapter
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_detail_view.*
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
class DetailViewFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var report: Report
    private var alertDialog: AlertDialog? = null
    private var disposable: Disposable? = null

    companion object {
        fun newInstance(): DetailViewFragment {
            return DetailViewFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = (activity as MainActivity).realm
        val reportId: Long? = arguments?.getLong("reportId")
        val r = realm.where<Report>().equalTo("timestamp", reportId).findFirst()
        if (r != null) {
            report = r
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_view, container, false)
        setHasOptionsMenu(true)
        setUpToolbar(view)
        return view
    }

    private fun setUpToolbar(view: View) {
        val toolBar: Toolbar = (view.findViewById(R.id.toolbar)) as Toolbar
        toolBar.title = getString(R.string.details_title)
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayReport()
        deleteReportButton.setOnClickListener { onDeleteReport(report.timestamp) }
        fab_send_report.setOnClickListener { aView -> onSendReport(aView) }
    }

    private fun displayReport() {
        Picasso
                .with(context)
                .load(Utils.getStreetViewUrl(600, 600, report.actualLatitude, report.actualLongitude))
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(locationImage)
        report_time_tv.text = Utils.toReadableFormat(report.timestamp)
        report_gsp_location_tv.text = String.format(getString(R.string.location_formatted), report.actualLatitude, report.actualLongitude, report.gspAccuracy)
        report_measured_location_tv.text = String.format(getString(R.string.location_formatted), report.assumedLatitude, report.assumedLongitude, report.assumedAccuracy)
        report_acc_diff_tv.text = String.format(getString(R.string.accuracy_difference), report.accuracyDifference)
        setUpTowerRecyclerView()
        setUpAccessPointsRecyclerView()
    }

    private fun setUpTowerRecyclerView() {
        towersRecyclerView.layoutManager = LinearLayoutManager(context)
        towersRecyclerView.setHasFixedSize(true)
        towersRecyclerView.adapter = CellTowersAdapter(context, report.towerMeasurements)
    }

    private fun setUpAccessPointsRecyclerView() {
        accessPointsRecyclerView.layoutManager = LinearLayoutManager(context)
        accessPointsRecyclerView.setHasFixedSize(true)
        accessPointsRecyclerView.adapter = AccessPointsAdapter(context, report.pointMeasurements)
    }

    private fun onSendReport(view: View) {
        Timber.d("Sending report as e-mail attachment")
    }

    private fun onDeleteReport(reportId: Long) {
        alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(getString(R.string.dialog_delete_message))
                .setNegativeButton(getString(R.string.dialog_delete_negative), { dialog, _ -> dialog.dismiss() })
                .setPositiveButton(getString(R.string.dialog_delete_positive),
                        { dialog, _ ->
                            disposable = Observable.just(true)
                                    .subscribeOn(Schedulers.io())
                                    .map {
                                        Realm.getInstance(Utils.getNormalRealmConfig()).use {
                                            val r = it.where<Report>().equalTo("timestamp", reportId).findFirst()
                                            it.executeTransaction { _ ->
                                                r?.deleteFromRealm()
                                            }
                                        }
                                    }
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        dialog.dismiss()
                                        activity.onBackPressed()
                                    })

                        })
                .create()
        alertDialog?.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss()
        disposable?.dispose()
    }
}