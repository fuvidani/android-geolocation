package at.ac.tuwien.mnsa.geolocation.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.location.LocationRequest;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
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
  private final LocationManager locationManager;
  private static final long GPS_SETTLE_TIME = 20 * 1000; // in ms
  private static final int MAX_RETRIES = 5;
  private static final long MAX_LOCATION_AGE = GPS_SETTLE_TIME + 5 * 1000; // in ms

  public GPSLocationService(Context context) {
    this.context = context;
    this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }

  public Observable<Location> getLocation() {
    return Observable.defer(() -> {
      Location loc = fetchLocation();
      if (loc == null) {
        return Observable.error(new RuntimeException("Could not get location"));
      } else {
        return Observable.just(loc);
      }
    }).retry(MAX_RETRIES);
  }

  private Location fetchLocation() {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {

      BlockingQueue<Location> locations = new ArrayBlockingQueue<>(1);

      LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
          locations.offer(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
      };
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

      Location location = null;
      try {
        location = locations.poll(GPS_SETTLE_TIME, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        // Alright, lets fall back to emergency handling
      } finally {
        locationManager.removeUpdates(listener);
      }

      if (location == null) {
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        long timeSinceLastKnown =
            SystemClock.elapsedRealtimeNanos() - lastKnownLocation.getElapsedRealtimeNanos();
        if (timeSinceLastKnown <= MAX_LOCATION_AGE * 1000 * 1000) {
          location = lastKnownLocation;
        }
      }

      return location;
    } else {
      throw new RuntimeException("No permission to access location");
    }
  }
}
