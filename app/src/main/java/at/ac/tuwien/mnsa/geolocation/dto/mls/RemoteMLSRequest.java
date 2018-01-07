package at.ac.tuwien.mnsa.geolocation.dto.mls;

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


public class RemoteMLSRequest {
  public List<RemoteMLSWifi> wifiAccessPoints;
  public List<RemoteMLSCellTower> cellTowers;
}
