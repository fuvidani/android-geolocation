package at.ac.tuwien.mnsa.geolocation.dto;

import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSRequest;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSResponse;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */


public class MLSLocationInformation {
  private final RemoteMLSRequest request;
  private final RemoteMLSResponse response;

  public MLSLocationInformation(RemoteMLSRequest request,
      RemoteMLSResponse response) {
    this.request = request;
    this.response = response;
  }

  public RemoteMLSRequest getRequest() {
    return request;
  }

  public RemoteMLSResponse getResponse() {
    return response;
  }
}
