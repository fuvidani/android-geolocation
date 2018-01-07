package at.ac.tuwien.mnsa.geolocation.dto;

import android.location.Location;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */
public class ReportDraft {

  private final MLSLocationInformation mlsLocationInformation;
  private final Location gpsLocationInformation;

  public ReportDraft(
      MLSLocationInformation mlsLocationInformation,
      Location gpsLocationInformation) {
    this.mlsLocationInformation = mlsLocationInformation;
    this.gpsLocationInformation = gpsLocationInformation;
  }

  public MLSLocationInformation getMlsLocationInformation() {
    return mlsLocationInformation;
  }

  public Location getGpsLocationInformation() {
    return gpsLocationInformation;
  }
}
