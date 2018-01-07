package at.ac.tuwien.mnsa.geolocation.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import io.reactivex.Observable;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */


public class GPSLocationService {

  private final Context context;
  private LocationRequest request = LocationRequest.create()
      .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

  public GPSLocationService(Context context) {
    this.context = context;
  }

  @SuppressLint("MissingPermission")
  public Observable<Location> getLocation() {
    ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
    return locationProvider.getUpdatedLocation(request);
  }

}
