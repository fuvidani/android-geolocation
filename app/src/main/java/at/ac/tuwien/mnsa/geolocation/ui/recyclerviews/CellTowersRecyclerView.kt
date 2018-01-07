package at.ac.tuwien.mnsa.geolocation.ui.recyclerviews

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class CellTowersAdapter(private val context: Context,
                        private val towerMeasurements: List<CellTowerMeasurement>)
    : RecyclerView.Adapter<CellTowerViewHolder>() {

    override fun getItemCount(): Int {
        return towerMeasurements.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CellTowerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.tower_measurement_entry, parent, false)
        return CellTowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CellTowerViewHolder, position: Int) {

    }
}


class CellTowerViewHolder(private val view: View) : RecyclerView.ViewHolder(view)