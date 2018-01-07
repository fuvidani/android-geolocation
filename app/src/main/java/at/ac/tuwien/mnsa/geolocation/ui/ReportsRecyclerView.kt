package at.ac.tuwien.mnsa.geolocation.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import at.ac.tuwien.mnsa.geolocation.dto.ReportDetailClickEvent
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.recyclerview_entry.view.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*

/**
 * <h4>About this class</h4>
 *
 * <p>Description</p>
 *
 * @author Daniel Fuevesi
 * @since 0.1.0
 * @version 0.1.0
 */
class ReportsAdapter(
        data: OrderedRealmCollection<Report>?,
        autoUpdate: Boolean, private val context: Context) : RealmRecyclerViewAdapter<Report, ReportViewHolder>(data, autoUpdate) {

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = getItem(position)
        if (report != null) {
            Picasso
                    .with(context)
                    .load(Utils.getStreetViewUrl(600, 300, report.actualLatitude, report.actualLongitude))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.itemView.cardImage)
            holder.itemView.details_button.setOnClickListener { EventBus.getDefault().post(ReportDetailClickEvent(report.timestamp)) }
            holder.itemView.measurementLocationTv.text = String.format(context.getString(R.string.actual_location), report.actualLatitude, report.actualLongitude)
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = report.timestamp
            val formattedDate = DateFormat.format("dd. MMMM yyyy HH:mm:ss", cal).toString()
            holder.itemView.cardTitle.text = formattedDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.recyclerview_entry, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        if (item != null) {
            return item.timestamp
        }
        Timber.e("getItem(position) returned null!!")
        return 0L
    }
}

class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        Timber.d("Click")
    }

}