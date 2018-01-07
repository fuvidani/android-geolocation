package at.ac.tuwien.mnsa.geolocation.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.*
import at.ac.tuwien.mnsa.geolocation.service.ReportService
import at.ac.tuwien.mnsa.geolocation.ui.recyclerviews.ReportsAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class MainFragment : Fragment() {

    private var alertDialog: AlertDialog? = null
    private var disposable: Disposable? = null

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        EventBus.getDefault().register(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        fab_add_report.show()
        setUpRecyclerView()
        observeFabClick(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_menu_main, menu)
    }

    private fun setUpToolbar() {
        toolbar.title = getString(R.string.overview_title)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val realm = (activity as MainActivity).realm
        val adapter = ReportsAdapter(
                realm.where<Report>()
                        .sort("timestamp", Sort.DESCENDING)
                        .findAll(), true, context)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                layoutManager.scrollToPosition(0)
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fab_add_report.hide()
        recyclerView.adapter = null
        EventBus.getDefault().unregister(this)
        alertDialog?.dismiss()
        disposable?.dispose()
    }

    private fun observeFabClick(view: View) {
        RxView.clicks(fab_add_report)
                .throttleFirst(1, TimeUnit.SECONDS)
                .bindToLifecycle(view)
                .map { setLoading(true) }
                .delay(1, TimeUnit.SECONDS)
                .map { context.startService(Intent(context, ReportService::class.java)) }
                .subscribe()
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            fab_add_report.hide()
        } else {
            progressBar.visibility = View.GONE
            fab_add_report.show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewReport(event: ReportGeneratedEvent) {
        setLoading(false)
        Snackbar.make(fab_add_report, R.string.new_report_snackbar_msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.new_report_snackbar_action, { EventBus.getDefault().post(ReportDetailClickEvent(event.reportId)) })
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGenerationError(event: ReportGenerationError) {
        setLoading(false)
        Snackbar.make(fab_add_report, R.string.generation_error, Snackbar.LENGTH_LONG).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeleteReport(event: ReportDeleteClickEvent) {
        val reportId = event.reportId
        alertDialog = AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(getString(R.string.dialog_delete_message))
                .setNegativeButton(getString(R.string.dialog_delete_negative), { dialog, _ -> dialog.dismiss() })
                .setPositiveButton(getString(R.string.dialog_delete_positive),
                        { dialog, _ ->
                            dialog.dismiss()
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
                                    .subscribe()

                        })
                .create()
        alertDialog?.show()
    }
}