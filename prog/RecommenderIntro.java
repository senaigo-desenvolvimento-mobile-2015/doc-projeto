package site.fabricionogueira.androidclient.http;

import com.google.firebase.iid.FirebaseInstanceId;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import site.fabricionogueira.androidclient.BuildConfig;

/**
 * Gerenciador para as requisições ao serviço.
 *
 * @author Fabricio Nogueira <nogsantos@gmail.com>
 * @since 19/03/2017
 */
public class Service {

    /**
     * Cria um cabeçalho para as requisições
     *
     * @return OkHttpClient Cabeçalho construido
     */
    private static OkHttpClient header() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        /*
         * Log
         */
        if (BuildConfig.LOG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
        httpClient.addInterceptor(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json; charset=utf-8")
                    .addHeader("Authorization", FirebaseInstanceId.getInstance().getToken())
                    .addHeader("App-authorization", BuildConfig.shared_name)
                    .build();
            return chain.proceed(request);
        });
        return httpClient.build();
    }

    /**
     * Realiza a requisição
     *
     * @return Retrofit
     */
    public static Retrofit request() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.service_address)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .client(header())
                .build();
    }

}
