package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_main.*
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

    private var adapter: ReportsAdapter? = null
    private var realm: Realm? = null

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        realm = (activity as MainActivity).realm
        setUpRecyclerView()
        addAndDeleteDummyReport()
    }

    private fun setUpToolbar() {
        toolbar.title = getString(R.string.overview_title)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ReportsAdapter(realm?.where<Report>()?.findAll(), true, context)
        recyclerView.adapter = adapter
    }

    // TODO delete this if not necessary anymore
    private fun addAndDeleteDummyReport() {
        Observable
                .timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    Realm.getInstance(Utils.getNormalRealmConfig()).use {
                        it.executeTransaction { realm ->
                            val report = Report()
                            report.timestamp = System.currentTimeMillis()
                            report.actualLatitude = 48.1739176
                            report.actualLongitude = 16.3786159
                            realm.copyToRealmOrUpdate(report)
                        }
                    }
                }
                .subscribe()
        Observable
                .timer(15000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    Realm.getInstance(Utils.getNormalRealmConfig()).use {
                        it.executeTransaction { realm ->
                            realm.delete(Report::class.java)
                        }
                    }
                }
                .subscribe()
    }
}