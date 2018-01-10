package at.ac.tuwien.mnsa.geolocation.persistence;

import android.content.Context;
import android.location.Location;
import at.ac.tuwien.mnsa.geolocation.Utils;
import at.ac.tuwien.mnsa.geolocation.dto.AccessPointMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.CellTowerMeasurement;
import at.ac.tuwien.mnsa.geolocation.dto.Report;
import at.ac.tuwien.mnsa.geolocation.dto.ReportDraft;
import at.ac.tuwien.mnsa.geolocation.dto.ReportGeneratedEvent;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSCellTower;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSWifi;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmConfiguration.Builder;
import io.realm.RealmList;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

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
  private String key;
  private static final String ENCRYPTION_FILE_PREFIX = "encrypted_";

  public PersistenceManager(Context context) {
    this.context = context;
  }

  public void persistReport(ReportDraft reportDraft) {
    final long reportId = System.currentTimeMillis();
    try (Realm realm = Realm.getInstance(Utils.Companion.getNormalRealmConfig())) {
      realm.executeTransaction(r -> {
        Report report = new Report();

        report.setTimestamp(reportId);

        report.setActualLatitude(reportDraft.getGpsLocationInformation().getLatitude());
        report.setActualLongitude(reportDraft.getGpsLocationInformation().getLongitude());
        report.setGspAccuracy(reportDraft.getGpsLocationInformation().getAccuracy());

        report
            .setAssumedLatitude(reportDraft.getMlsLocationInformation().getResponse().location.lat);
        report.setAssumedLongitude(
            reportDraft.getMlsLocationInformation().getResponse().location.lng);
        report.setAssumedAccuracy(reportDraft.getMlsLocationInformation().getResponse().accuracy);

        RealmList<AccessPointMeasurement> accessPointMeasurements = new RealmList<>();
        RealmList<CellTowerMeasurement> cellTowerMeasurements = new RealmList<>();

        for (RemoteMLSWifi wifi : reportDraft.getMlsLocationInformation()
            .getRequest().wifiAccessPoints) {
          AccessPointMeasurement measurement = new AccessPointMeasurement();

          measurement.setAddress(wifi.macAddress);
          measurement.setFrequency(wifi.frequency);
          measurement.setStrength(wifi.signalStrength);

          accessPointMeasurements.add(measurement);
        }

        for (RemoteMLSCellTower cellTower : reportDraft.getMlsLocationInformation()
            .getRequest().cellTowers) {
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
        mlsLocation
            .setLongitude(reportDraft.getMlsLocationInformation().getResponse().location.lng);
        report
            .setPositionDifference(reportDraft.getGpsLocationInformation().distanceTo(mlsLocation));

        r.copyToRealmOrUpdate(report);
      });
    }
    EventBus.getDefault().post(new ReportGeneratedEvent(reportId));
  }

  private RealmConfiguration getEncryptedRealmConfig(byte[] hashedKey) {
    return new Builder().deleteRealmIfMigrationNeeded().name(ENCRYPTION_FILE_PREFIX + getNormalRealmConfig().getRealmFileName()).encryptionKey(hashedKey).build();
  }

  private RealmConfiguration getNormalRealmConfig() {
    return new Builder().deleteRealmIfMigrationNeeded().build();
  }

  public Realm getRealmDatabase(String key) {
    if (key == null) {
      return Realm.getInstance(getNormalRealmConfig());
    } else {
      return Realm.getInstance(getEncryptedRealmConfig(get_SHA_512_SecurePassword(key,"salt").getBytes()));
    }
  }

  public String get_SHA_512_SecurePassword(String passwordToHash, String salt){
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt.getBytes("UTF-8"));
      byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
      StringBuilder sb = new StringBuilder();
      for(int i=0; i< bytes.length ;i++){
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedPassword = sb.toString();
    }
    catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
      Timber.e(e);
    }
    return generatedPassword;
  }

  private Realm createEncryptedRealm(byte[] hashedKey) {
    RealmConfiguration normalConfig = getNormalRealmConfig();


    RealmConfiguration encryptedConfig = getEncryptedRealmConfig(hashedKey);

    migrationIfNeeded(normalConfig, encryptedConfig);

    return Realm.getInstance(encryptedConfig);
  }

  public void encryptDatabase(String key) {
    // set key
    this.key = key;

    createEncryptedRealm(get_SHA_512_SecurePassword(key,"salt").getBytes());
  }

  private void migrationIfNeeded(RealmConfiguration unencryptedConfig, RealmConfiguration encryptedConfig) {
    File unencryptedFile = new File(unencryptedConfig.getPath());
    File encryptedFile = new File(encryptedConfig.getPath());

    Realm unencryptedRealm = null;
    if (!encryptedFile.exists() && unencryptedFile.exists()) {
      try {
        unencryptedRealm = Realm.getInstance(unencryptedConfig);
        unencryptedRealm.writeEncryptedCopyTo(encryptedFile, encryptedConfig.getEncryptionKey());
      } finally {
        if (unencryptedRealm != null) {
          unencryptedRealm.close();
        }
      }

      // delete original database
      Realm.deleteRealm(getNormalRealmConfig());
    }
  }
}
