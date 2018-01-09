package at.ac.tuwien.mnsa.geolocation.service;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
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
public class WifiInformationRetriever {

  private final Context context;

  public WifiInformationRetriever(Context context) {
    this.context = context;
  }

  public Observable<List<ScanResult>> getInformation() {
    if (permissionGranted()) {
      final WifiManager wifiManager = (WifiManager) context.getApplicationContext()
          .getSystemService(Context.WIFI_SERVICE);

      return Observable.defer(() -> Observable.just(wifiManager.getScanResults()));
    } else {
      Timber.e("No WIFI_STATE or GPS permission granted");
      return Observable.defer(() -> Observable.just(new ArrayList<>()));

    }
  }

  private boolean permissionGranted() {
    return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE)
        == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED);
  }
}
