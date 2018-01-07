package at.ac.tuwien.mnsa.geolocation.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import at.ac.tuwien.mnsa.geolocation.dto.CellTower;
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
public class CellInformationRetriever {

  private final Context context;

  public CellInformationRetriever(Context context) {
    this.context = context;
  }

  public Observable<List<CellTower>> getInformation() {
    if (permissionGranted()) {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
      if (cellInfoList == null || cellInfoList.isEmpty()) {
        Timber.e("AllCellInfo returned null or empty list");
        return Observable.defer(() -> Observable.just(new ArrayList<>()));
      } else {
        List<CellTower> towers = new ArrayList<>();
        for (CellInfo info: cellInfoList) {
          towers.add(CellInfoParser.Companion.parseCellInfo(info));
        }
        return Observable.defer(() -> Observable.just(towers));
      }
    } else {
      Timber.e("No GPS permission granted");
      return Observable.defer(() -> Observable.just(new ArrayList<>()));
    }
  }

  private boolean permissionGranted() {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;
  }
}
