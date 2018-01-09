package at.ac.tuwien.mnsa.geolocation.service;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import com.google.android.gms.location.LocationRequest;
import io.reactivex.Observable;
import java.util.concurrent.TimeUnit;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import timber.log.Timber;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */
public class GPSLocationService {

  private final Context context;
  private LocationRequest request =
      LocationRequest.create()
          .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
          .setInterval(1000);

  public GPSLocationService(Context context) {
    this.context = context;
  }

  public Observable<Location> getLocation() {
    ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
    return locationProvider
        .getUpdatedLocation(request)
        .filter(
            location -> {
              final long diff =
                  TimeUnit.NANOSECONDS.toMillis(
                      (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()));
              Timber.d("Location age: " + diff + " milliseconds");
              return diff < (20 * 1000);
            });
  }
}
