package cz.patrik.stanko.climbinglogger.data

import android.content.Context
import androidx.room.Room
import cz.patrik.stanko.climbinglogger.BuildConfig
import cz.patrik.stanko.climbinglogger.data.local.ClimbDatabase
import cz.patrik.stanko.climbinglogger.data.remote.ClimbingApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceLocator {

    private const val BASE_URL = "https://trailapi-trailapi.p.rapidapi.com/"

    lateinit var repository: ClimbingRepository
        private set

    fun init(context: Context) {
        if (::repository.isInitialized) return

        val db = Room.databaseBuilder(
            context.applicationContext,
            ClimbDatabase::class.java,
            "climb_logger.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val rapidApiInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("x-rapidapi-host", "trailapi-trailapi.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                    .build()
                return chain.proceed(request)
            }
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(rapidApiInterceptor)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ClimbingApiService::class.java)

        repository = ClimbingRepository(
            sessionDao = db.sessionDao(),
            routeDao = db.routeDao(),
            api = api
        )
    }
}
