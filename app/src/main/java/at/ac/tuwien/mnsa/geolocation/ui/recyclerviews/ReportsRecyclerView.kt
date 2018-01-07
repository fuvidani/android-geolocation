package at.ac.tuwien.mnsa.geolocation.ui.recyclerviews

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.ac.tuwien.mnsa.geolocation.R
import at.ac.tuwien.mnsa.geolocation.Utils
import at.ac.tuwien.mnsa.geolocation.dto.Report
import at.ac.tuwien.mnsa.geolocation.dto.ReportDeleteClickEvent
import at.ac.tuwien.mnsa.geolocation.dto.ReportDetailClickEvent
import com.squareup.picasso.Picasso
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.recyclerview_entry.view.*
import org.greenrobot.eventbus.EventBus
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
class ReportsAdapter(
        data: OrderedRealmCollection<Report>?,
        autoUpdate: Boolean, private val context: Context) : RealmRecyclerViewAdapter<Report, ReportViewHolder>(data, autoUpdate) {

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = getItem(position)
        if (report != null) {
            holder.reportId = report.timestamp
            Picasso
                    .with(context)
                    .load(Utils.getStreetViewUrl(600, 300, report.actualLatitude, report.actualLongitude))
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.itemView.cardImage)
            holder.itemView.details_button.setOnClickListener { EventBus.getDefault().post(ReportDetailClickEvent(report.timestamp)) }
            holder.itemView.delete_button.setOnClickListener { EventBus.getDefault().post(ReportDeleteClickEvent(report.timestamp)) }
            holder.itemView.measurementAccuracyDifferenceTv.text = String.format(context.getString(R.string.overview_card_text), report.accuracyDifference.toString())
            holder.itemView.cardTitle.text = Utils.toReadableFormat(report.timestamp)
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

class ReportViewHolder(itemView: View, var reportId: Long = 0) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        EventBus.getDefault().post(ReportDetailClickEvent(reportId))
    }
}