package cz.patrik.stanko.climbinglogger.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service pro TrailAPI přes RapidAPI.
 *
 * GET https://trailapi-trailapi.p.rapidapi.com/activity/
 *
 * Sample (podle dokumentace a ukázek):
 *  - lat, lon = souřadnice
 *  - q-activities_activity_type_name_eq = "hiking"
 *  - q-state_cont = např. "California"
 */
interface ClimbingApiService {

    @GET("activity/")
    suspend fun getHikingPlaces(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Int = 50,
        @Query("limit") limit: Int = 20,
        @Query("q-activities_activity_type_name_eq")
        activityType: String = "hiking",
        @Query("q-state_cont")
        state: String? = null
    ): Map<String, TrailPlaceRaw>
}
