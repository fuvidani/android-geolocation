package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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

    private lateinit var adapter: ReportsAdapter
    private lateinit var realm: Realm

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        fab_add_report.show()
        realm = (activity as MainActivity).realm
        setUpRecyclerView()
        observeFabClicks(view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_menu_main, menu)
    }

    private fun setUpToolbar() {
        toolbar.title = getString(R.string.overview_title)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun setUpRecyclerView() {
        adapter = ReportsAdapter(realm.where<Report>()
                .sort("timestamp", Sort.DESCENDING)
                .findAll(), true, context)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                layoutManager.smoothScrollToPosition(recyclerView, null, 0)
            }
        })
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fab_add_report.hide()
        recyclerView.adapter = null
    }

    private fun observeFabClicks(view: View) {
        RxView.clicks(fab_add_report)
                .throttleFirst(1, TimeUnit.SECONDS)
                .bindToLifecycle(view)
                .map {
                    progressBar.visibility = View.VISIBLE
                    fab_add_report.hide()
                }
                .switchMap {
                    Observable
                            .timer(3000, TimeUnit.MILLISECONDS)
                            .bindToLifecycle(view)
                            .map {
                                Realm.getInstance(Utils.getNormalRealmConfig()).use {
                                    it.executeTransaction { realm ->
                                        val report = Report()
                                        report.timestamp = System.currentTimeMillis()
                                        report.actualLatitude = 47.5149429
                                        report.actualLongitude = 19.0778626
                                        realm.copyToRealmOrUpdate(report)
                                    }
                                }
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .map {
                                progressBar.visibility = View.GONE
                                fab_add_report.show()
                            }
                }
                .subscribe()
    }
}