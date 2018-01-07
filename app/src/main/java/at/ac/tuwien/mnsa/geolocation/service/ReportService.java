package at.ac.tuwien.mnsa.geolocation.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import at.ac.tuwien.mnsa.geolocation.GeoLocationApp;
import javax.inject.Inject;
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
public class ReportService extends IntentService {

  @Inject
  ReportGenerator reportGenerator;

  @Inject
  PersistenceManager persistenceManager;

  public ReportService() {
    super(ReportService.class.getName());
  }

  @Override
  public void onCreate() {
    super.onCreate();

    ((GeoLocationApp) getApplication()).getApplicationComponent().inject(this);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    reportGenerator.generateReport().subscribe(
        result -> persistenceManager.persistReport(result),
        Timber::e,
        () -> Timber.d("Completed"));
  }
}
