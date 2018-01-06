package at.ac.tuwien.mnsa.geolocation.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import io.realm.Realm

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

    companion object {
        fun newInstance(): DetailViewFragment {
            return DetailViewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_view, container, false)
        setHasOptionsMenu(true)
        setUpToolbar()
        return view
    }

    private fun setUpToolbar() {
        val toolBar: Toolbar = (view?.findViewById(R.id.toolbar)) as Toolbar
        toolBar.title = getString(R.string.details_title)
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = (activity as MainActivity).realm
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}