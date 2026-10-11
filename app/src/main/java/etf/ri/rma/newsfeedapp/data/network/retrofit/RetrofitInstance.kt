package etf.ri.rma.newsfeedapp.data.network.retrofit

import etf.ri.rma.newsfeedapp.BuildConfig
import etf.ri.rma.newsfeedapp.data.network.api.ImagaApiService
import etf.ri.rma.newsfeedapp.data.network.api.NewsApiService
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    private val NEWS_API_BASE_URL = "https://api.thenewsapi.com/v1/"
    private val IMAGGA_API_BASE_URL = "https://api.imagga.com/v2/"

    private val newsApiRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NEWS_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val imaggaRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(IMAGGA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Basic YOUR_IMAGGA_API_KEY")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
    }

    val newsApi: NewsApiService by lazy {
        newsApiRetrofit.create(NewsApiService::class.java)
    }

    val imaggaApi: ImagaApiService by lazy {
        imaggaRetrofit.create(ImagaApiService::class.java)
    }
}

class RetrofitInstanceNews {
    companion object {
        private const val NEWS_LIST_BASE_URL = "https://api.thenewsapi.com/"

        private val retrofit by lazy {
            retrofit2.Retrofit.Builder()
                .baseUrl(NEWS_LIST_BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
        }

        val api: NewsApiService by lazy {
            retrofit.create(NewsApiService::class.java)
        }
    }
}

class RetrofitInstanceImaga {
    companion object {
        private const val BASE_URL = "https://api.imagga.com/"

        private val okHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val originalRequest = chain.request()

                    val auth = Credentials.basic(BuildConfig.IMAGGA_API_KEY, BuildConfig.IMAGGA_API_SECRET)

                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", auth)
                        .build()

                    chain.proceed(newRequest)
                })
                .build()
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: ImagaApiService by lazy {
            retrofit.create(ImagaApiService::class.java)
        }
    }
}