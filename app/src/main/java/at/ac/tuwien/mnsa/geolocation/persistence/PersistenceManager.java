package at.ac.tuwien.mnsa.geolocation.persistence;

import android.content.Context;
import android.location.Location;
import at.ac.tuwien.mnsa.geolocation.Utils;
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.Report;
import at.ac.tuwien.mnsa.geolocation.dto.ReportDraft;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSCellTower;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSWifi;
import io.realm.Realm;
import io.realm.RealmList;

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

  public void persistReport(ReportDraft reportDraft) {
    try (Realm realm = Realm.getInstance(Utils.Companion.getNormalRealmConfig())) {
      realm.executeTransaction(r -> {
        Report report = new Report();

        report.setTimestamp(System.currentTimeMillis());

        report.setActualLatitude(reportDraft.getGpsLocationInformation().getLatitude());
        report.setActualLongitude(reportDraft.getGpsLocationInformation().getLongitude());
        report.setGspAccuracy(reportDraft.getGpsLocationInformation().getAccuracy());

        report.setAssumedLatitude(reportDraft.getMlsLocationInformation().getResponse().location.lat);
        report.setAssumedLongitude(reportDraft.getMlsLocationInformation().getResponse().location.lng);
        report.setAssumedAccuracy(reportDraft.getMlsLocationInformation().getResponse().accuracy);

        RealmList<AccessPointMeasurement> accessPointMeasurements = new RealmList<>();
        RealmList<CellTowerMeasurement> cellTowerMeasurements = new RealmList<>();

        for (RemoteMLSWifi wifi: reportDraft.getMlsLocationInformation().getRequest().wifiAccessPoints) {
          AccessPointMeasurement measurement = new AccessPointMeasurement();

          measurement.setAddress(wifi.macAddress);
          measurement.setFrequency(wifi.frequency);
          measurement.setStrength(wifi.signalStrength);

          accessPointMeasurements.add(measurement);
        }

        for (RemoteMLSCellTower cellTower: reportDraft.getMlsLocationInformation().getRequest().cellTowers) {
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

        Location mlsLocation = new Location("");
        mlsLocation.setLatitude(reportDraft.getMlsLocationInformation().getResponse().location.lat);
        mlsLocation.setLongitude(reportDraft.getMlsLocationInformation().getResponse().location.lng);
        report.setAccuracyDifference(reportDraft.getGpsLocationInformation().distanceTo(mlsLocation));

        r.copyToRealmOrUpdate(report);
      });
    }
  }
}
