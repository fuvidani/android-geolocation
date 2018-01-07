package at.ac.tuwien.mnsa.geolocation.di;

import android.content.Context;
import at.ac.tuwien.mnsa.geolocation.service.CellInformationRetriever;
import at.ac.tuwien.mnsa.geolocation.service.GPSLocationService;
import at.ac.tuwien.mnsa.geolocation.service.RemoteMozillaLocationService;
import at.ac.tuwien.mnsa.geolocation.service.ReportGenerator;
import at.ac.tuwien.mnsa.geolocation.service.WifiInformationRetriever;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
public class ServiceModule {
  private static final String MLS_URL = "https://location.services.mozilla.com/v1/";
  private final OkHttpClient.Builder httpClient;

  public ServiceModule() {
    // set http logger
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(Level.BODY);
    httpClient = new OkHttpClient.Builder();
    httpClient.addInterceptor(logging);

    httpClient.interceptors().add(chain -> {
      Request request = chain.request();
      HttpUrl url = request.url().newBuilder().addQueryParameter("key","test").build();
      request = request.newBuilder().url(url).build();
      return chain.proceed(request);
    });
  }

  @Provides
  @Singleton
  WifiInformationRetriever provideWifiInformationRetriever(Context context) {
    return new WifiInformationRetriever(context);
  }

  @Provides
  @Singleton
  CellInformationRetriever provideCellInformationRetriever(Context context) {
    return new CellInformationRetriever(context);
  }

  @Provides
  @Singleton
  ReportGenerator provideReportGenerator(WifiInformationRetriever wifiInformationRetriever, CellInformationRetriever cellInformationRetriever, RemoteMozillaLocationService remoteMozillaLocationService, GPSLocationService gpsLocationService) {
    return new ReportGenerator(wifiInformationRetriever, cellInformationRetriever,
        remoteMozillaLocationService, gpsLocationService);
  }

  @Provides
  @Singleton
  GPSLocationService provideGPSLocationService(Context context) {
    return new GPSLocationService(context);
  }

  @Provides
  @Singleton
  RemoteMozillaLocationService provideRemoteMozillaLocationService(ObjectMapper objectMapper) {
    return new Retrofit.Builder()
        .client(httpClient.build())
        .baseUrl(MLS_URL)
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(RemoteMozillaLocationService.class);
  }

  @Provides
  @Singleton
  ObjectMapper provideObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }
}
