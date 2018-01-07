package at.ac.tuwien.mnsa.geolocation.service;

import android.content.Context;
import at.ac.tuwien.mnsa.geolocation.Utils;
import at.ac.tuwien.mnsa.geolocation.dto.Report;
import at.ac.tuwien.mnsa.geolocation.dto.ReportGeneratedEvent;
import at.ac.tuwien.mnsa.geolocation.dto.ReportTemplate;
import io.realm.Realm;
import org.greenrobot.eventbus.EventBus;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */


public class PersistenceManager {
  private final Context context;

  public PersistenceManager(Context context) {
    this.context = context;
  }

  public void persistReport(ReportTemplate reportTemplate) {
    final long reportId = System.currentTimeMillis();
    try (Realm realm = Realm.getInstance(Utils.Companion.getNormalRealmConfig())) {
      realm.executeTransaction(r -> {
        Report report = new Report();

        report.setTimestamp(reportId);

        report.setActualLatitude(reportTemplate.getGpsLocationInformation().getLatitude());
        report.setActualLongitude(reportTemplate.getGpsLocationInformation().getLongitude());
        report.setGspAccuracy(reportTemplate.getGpsLocationInformation().getAccuracy());

        report.setAssumedLatitude(reportTemplate.getMlsLocationInformation().getResponse().location.lat);
        report.setActualLongitude(reportTemplate.getMlsLocationInformation().getResponse().location.lng);
        report.setAssumedAccuracy(reportTemplate.getMlsLocationInformation().getResponse().accuracy);

        r.copyToRealmOrUpdate(report);
      });
    }
    EventBus.getDefault().post(new ReportGeneratedEvent(reportId));
  }
}
