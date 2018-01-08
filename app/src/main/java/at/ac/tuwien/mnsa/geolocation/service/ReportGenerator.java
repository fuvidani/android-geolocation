package at.ac.tuwien.mnsa.geolocation.service;

import android.location.Location;
import android.net.wifi.ScanResult;
import at.ac.tuwien.mnsa.geolocation.dto.CellTower;
import at.ac.tuwien.mnsa.geolocation.dto.MLSLocationInformation;
import at.ac.tuwien.mnsa.geolocation.dto.NetworkInformation;
import at.ac.tuwien.mnsa.geolocation.dto.ReportDraft;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSCellTower;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSRequest;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSWifi;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
public class ReportGenerator {

  private final WifiInformationRetriever wifiInformationRetriever;
  private final CellInformationRetriever cellInformationRetriever;
  private final RemoteMozillaLocationService remoteMozillaLocationService;
  private final GPSLocationService gpsLocationService;

  public ReportGenerator(
      WifiInformationRetriever wifiInformationRetriever,
      CellInformationRetriever cellInformationRetriever,
      RemoteMozillaLocationService remoteMozillaLocationService,
      GPSLocationService gpsLocationService) {
    this.wifiInformationRetriever = wifiInformationRetriever;
    this.cellInformationRetriever = cellInformationRetriever;
    this.remoteMozillaLocationService = remoteMozillaLocationService;
    this.gpsLocationService = gpsLocationService;
  }

  public Observable<ReportDraft> generateReport() {
    Observable<MLSLocationInformation> mlsLocationInformationObservable = getMLSLocation();
    Observable<Location> gpsLocationInformationObservable = getGPSLocation();

    return Observable
        .zip(mlsLocationInformationObservable, gpsLocationInformationObservable, ReportDraft::new);
  }

  private Observable<List<ScanResult>> getWifiInformation() {
    return wifiInformationRetriever.getInformation();
  }

  private Observable<List<CellTower>> getCellInformation() {
    return cellInformationRetriever.getInformation();
  }

  private Observable<NetworkInformation> fetchNetworkInformation() {
    final ObservableSource<List<ScanResult>> wifiInformationObservable = getWifiInformation();
    final ObservableSource<List<CellTower>> cellInformationObservable = getCellInformation();

    return Observable.zip(wifiInformationObservable, cellInformationObservable, NetworkInformation::new);
  }

  private Observable<MLSLocationInformation> getMLSLocation() {
    return fetchNetworkInformation().flatMap(networkInformation -> {

      final RemoteMLSRequest request = new RemoteMLSRequest();
      List<RemoteMLSWifi> wifiAccessPoints = new ArrayList<>();
      List<RemoteMLSCellTower> cellTowers = new ArrayList<>();

      for (ScanResult wifi: networkInformation.getWifiInformation()) {
        RemoteMLSWifi mlsWifi = new RemoteMLSWifi();
        mlsWifi.macAddress = wifi.BSSID;
        mlsWifi.signalStrength = wifi.level;
        mlsWifi.frequency = wifi.frequency;

        wifiAccessPoints.add(mlsWifi);
      }

      for (CellTower cellTower: networkInformation.getCellInformation()) {
        RemoteMLSCellTower mlsCellTower = new RemoteMLSCellTower();
        mlsCellTower.radioType = cellTower.getCellType().name().toLowerCase();
        mlsCellTower.mobileCountryCode = Long.parseLong(cellTower.getCountryCode());
        mlsCellTower.mobileNetworkCode = Long.parseLong(cellTower.getNetworkId());
        mlsCellTower.locationAreaCode = Long.parseLong(cellTower.getLocationAreaCode());
        mlsCellTower.cellId = Long.parseLong(cellTower.getCellId());
        mlsCellTower.signalStrength = cellTower.getSignalStrengthAsDbm();

        cellTowers.add(mlsCellTower);
      }

      request.wifiAccessPoints = wifiAccessPoints;
      request.cellTowers = cellTowers;

      return remoteMozillaLocationService.getLocation(request).map(response -> new MLSLocationInformation(request, response));
    });
  }

  private Observable<Location> getGPSLocation() {
    return gpsLocationService.getLocation();
  }
}
