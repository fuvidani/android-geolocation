package at.ac.tuwien.mnsa.geolocation.service;

import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSRequest;
import at.ac.tuwien.mnsa.geolocation.dto.mls.RemoteMLSResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */
public interface RemoteMozillaLocationService {

  @POST("geolocate")
  Observable<RemoteMLSResponse> getLocation(@Body RemoteMLSRequest request);
}
