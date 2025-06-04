package etf.ri.rma.newsfeedapp.data.network.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val NEWS_API_BASE_URL = "https://api.thenewsapi.com/v1/"
    private const val IMAGGA_BASE_URL = "https://api.imagga.com/v2/"

    private const val NEWS_API_KEY = "G6UF9BPiyYdqLY8TGZfjvsoWb0R5mKmfAoG3pNX1"
    private const val IMAGGA_API_KEY = "acc_1f155786164843c"
    private const val IMAGGA_SECRET = "32fde55dc7ab1c27b88197a5a834cb03"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val newsRetrofit = Retrofit.Builder()
        .baseUrl(NEWS_API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imaggaRetrofit = Retrofit.Builder()
        .baseUrl(IMAGGA_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val newsApiService: NewsApiService = newsRetrofit.create(NewsApiService::class.java)
    val imaggaService: ImagaApiService = imaggaRetrofit.create(ImagaApiService::class.java)

    fun getNewsApiKey() = NEWS_API_KEY
    fun getImaggaAuth() = "Basic " + android.util.Base64.encodeToString(
        "$IMAGGA_API_KEY:$IMAGGA_SECRET".toByteArray(),
        android.util.Base64.NO_WRAP
    )
}