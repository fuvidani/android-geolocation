package at.ac.tuwien.mnsa.geolocation.ui.recyclerviews

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement
import kotlinx.android.synthetic.main.tower_measurement_entry.view.*

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class CellTowersAdapter(private val towerMeasurements: List<CellTowerMeasurement>)
    : RecyclerView.Adapter<CellTowerViewHolder>() {

    override fun getItemCount(): Int {
        return towerMeasurements.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CellTowerViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.tower_measurement_entry, parent, false)
        return CellTowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CellTowerViewHolder, position: Int) {
        val cellTower = towerMeasurements[position % itemCount]
        holder.itemView.cellIdTv.text = cellTower.cellId
        holder.itemView.cellTypeTv.text = cellTower.cellType.toUpperCase()
        holder.itemView.countryCodeTv.text = cellTower.countryCode
        holder.itemView.networkIdTv.text = cellTower.networkId
        holder.itemView.locationAreaCodeTv.text = cellTower.locationAreaCode
        holder.itemView.strengthTv.text = cellTower.strength.toString()
    }
}

class CellTowerViewHolder(view: View) : RecyclerView.ViewHolder(view)