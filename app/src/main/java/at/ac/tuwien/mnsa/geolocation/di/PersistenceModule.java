package at.ac.tuwien.mnsa.geolocation.di;

import android.content.Context;
import at.ac.tuwien.mnsa.geolocation.service.PersistenceManager;
import at.ac.tuwien.mnsa.geolocation.service.WifiInformationRetriever;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * <h4>About this class</h4>
 *
 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 0.1.0
 * @since 0.1.0
 */
@Module
public class PersistenceModule {
  @Provides
  @Singleton
  PersistenceManager providePersistenceManager(Context context) {
    return new PersistenceManager(context);
  }
}
