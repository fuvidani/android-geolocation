package at.ac.tuwien.mnsa.geolocation.dto;

import android.net.wifi.ScanResult;
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


public class NetworkInformation {
  private final List<ScanResult> wifiInformation;
  private final List<CellTower> cellInformation;

  public NetworkInformation(List<ScanResult> wifiInformation,
      List<CellTower> cellInformation) {
    this.wifiInformation = wifiInformation;
    this.cellInformation = cellInformation;
  }

  public List<ScanResult> getWifiInformation() {
    return wifiInformation;
  }

  public List<CellTower> getCellInformation() {
    return cellInformation;
  }

}
