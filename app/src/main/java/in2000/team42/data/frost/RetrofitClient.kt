package in2000.team42.data.frost

import android.util.Base64
import android.util.Log
import com.google.gson.GsonBuilder
import in2000.team42.model.frost.FrostResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://frost.met.no/"
    private const val CLIENT_ID = "5fa50311-61ee-4aa0-8f29-2262c21212e5"
    private const val TAG = "RetrofitClient"

    val frostApiService: FrostApiService by lazy {
        val authToken = Base64.encodeToString("$CLIENT_ID:".toByteArray(), Base64.NO_WRAP)
        Log.d(TAG, "Authorization header set: Basic $authToken")

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Basic $authToken")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(FrostApiService::class.java)
    }
}