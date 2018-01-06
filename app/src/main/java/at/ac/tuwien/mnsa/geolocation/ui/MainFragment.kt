package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
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
        adapter = ReportsAdapter(realm?.where<Report>()?.sort("timestamp", Sort.DESCENDING)?.findAll(), true, context)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                layoutManager.smoothScrollToPosition(recyclerView, null, 0)
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    // TODO delete this if not necessary anymore
    private fun addAndDeleteDummyReport() {
        Observable
                .timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map {
                    Realm.getInstance(Utils.getNormalRealmConfig()).use {
                        it.executeTransaction { realm ->
                            val report = Report()
                            report.timestamp = 1515256840172
                            report.actualLatitude = 48.1739176
                            report.actualLongitude = 16.3786159
                            realm.copyToRealmOrUpdate(report)
                        }
                    }
                }
                .subscribe()
        Observable
                .timer(4000, TimeUnit.MILLISECONDS)
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