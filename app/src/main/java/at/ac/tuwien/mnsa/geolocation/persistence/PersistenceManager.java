package at.ac.tuwien.mnsa.geolocation.persistence;

import android.content.Context;
import at.ac.tuwien.mnsa.geolocation.Utils;
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.CellType;
import at.ac.tuwien.mnsa.geolocation.dto.Report;
import at.ac.tuwien.mnsa.geolocation.dto.ReportTemplate;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSCellTower;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSWifi;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;

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
    try (Realm realm = Realm.getInstance(Utils.Companion.getNormalRealmConfig())) {
      realm.executeTransaction(r -> {
        Report report = new Report();

        report.setTimestamp(System.currentTimeMillis());

        report.setActualLatitude(reportTemplate.getGpsLocationInformation().getLatitude());
        report.setActualLongitude(reportTemplate.getGpsLocationInformation().getLongitude());
        report.setGspAccuracy(reportTemplate.getGpsLocationInformation().getAccuracy());

        report.setAssumedLatitude(reportTemplate.getMlsLocationInformation().getResponse().location.lat);
        report.setAssumedLongitude(reportTemplate.getMlsLocationInformation().getResponse().location.lng);
        report.setAssumedAccuracy(reportTemplate.getMlsLocationInformation().getResponse().accuracy);

        RealmList<AccessPointMeasurement> accessPointMeasurements = new RealmList<>();
        RealmList<CellTowerMeasurement> cellTowerMeasurements = new RealmList<>();

        for (RemoteMLSWifi wifi: reportTemplate.getMlsLocationInformation().getRequest().wifiAccessPoints) {
          AccessPointMeasurement measurement = new AccessPointMeasurement();

          measurement.setAddress(wifi.macAddress);
          measurement.setFrequency(wifi.frequency);
          measurement.setStrength(wifi.signalStrength);

          accessPointMeasurements.add(measurement);
        }

        for (RemoteMLSCellTower cellTower: reportTemplate.getMlsLocationInformation().getRequest().cellTowers) {
          CellTowerMeasurement measurement = new CellTowerMeasurement();

          measurement.setCellId(String.valueOf(cellTower.cellId));
          measurement.setCellType(cellTower.radioType);
          measurement.setCountryCode(String.valueOf(cellTower.mobileCountryCode));
          measurement.setLocationAreaCode(String.valueOf(cellTower.locationAreaCode));
          measurement.setNetworkId(String.valueOf(cellTower.mobileNetworkCode));
          measurement.setStrength(cellTower.signalStrength);

          cellTowerMeasurements.add(measurement);
        }

        report.setPointMeasurements(accessPointMeasurements);
        report.setTowerMeasurements(cellTowerMeasurements);

        r.copyToRealmOrUpdate(report);
      });
    }
  }
}
