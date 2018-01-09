package at.ac.tuwien.mnsa.geolocation.ui.recyclerviews

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement
import kotlinx.android.synthetic.main.access_point_measurement_entry.view.*

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class AccessPointsAdapter(private val accessPointsMeasurements: List<AccessPointMeasurement>)
    : RecyclerView.Adapter<AccessPointViewHolder>() {

    override fun getItemCount(): Int {
        return accessPointsMeasurements.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AccessPointViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.access_point_measurement_entry, parent, false)
        return AccessPointViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccessPointViewHolder, position: Int) {
        val accessPoint = accessPointsMeasurements[position % itemCount]
        holder.itemView.addressTv.text = accessPoint.address
        holder.itemView.strengthTv.text = accessPoint.strength.toString()
        holder.itemView.frequencyTv.text = accessPoint.frequency.toString()
    }
}

class AccessPointViewHolder(view: View) : RecyclerView.ViewHolder(view)