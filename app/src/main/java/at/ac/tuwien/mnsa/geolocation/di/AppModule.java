package at.ac.tuwien.mnsa.geolocation.di;

import android.app.Application;
import android.content.Context;
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
public class AppModule {

  private final Application application;

  public AppModule(Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  Context provideApplicationContext() {
    return application;
  }
}
