package cz.patrik.stanko.climbinglogger.data

import cz.patrik.stanko.climbinglogger.data.local.ClimbRoute
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import cz.patrik.stanko.climbinglogger.data.local.RouteDao
import cz.patrik.stanko.climbinglogger.data.local.SessionDao
import cz.patrik.stanko.climbinglogger.data.remote.ClimbingApiService
import cz.patrik.stanko.climbinglogger.data.remote.ClimbingAreaDto
import cz.patrik.stanko.climbinglogger.data.remote.TrailPlaceRaw
import kotlinx.coroutines.flow.Flow

/**
 * Repository – lokální DB (Room) + vzdálené TrailAPI (hiking místa).
 */
class ClimbingRepository(
    private val sessionDao: SessionDao,
    private val routeDao: RouteDao,
    private val api: ClimbingApiService
) {

    // ------- Lokální log session / cest -------

    fun getSessions(): Flow<List<ClimbSession>> = sessionDao.getAllSessions()

    suspend fun addSession(date: String, locationName: String, isOutdoor: Boolean) {
        val session = ClimbSession(
            date = date,
            locationName = locationName,
            isOutdoor = isOutdoor
        )
        sessionDao.insertSession(session)
    }

    fun getRoutesForSession(sessionId: Long): Flow<List<ClimbRoute>> =
        routeDao.getRoutesForSession(sessionId)

    suspend fun addRoute(
        sessionId: Long,
        name: String,
        grade: String,
        style: String,
        isBoulder: Boolean,
        attempts: Int,
        note: String?
    ) {
        val route = ClimbRoute(
            sessionId = sessionId,
            name = name,
            grade = grade,
            style = style,
            isBoulder = isBoulder,
            attempts = attempts,
            note = note
        )
        routeDao.insertRoute(route)
    }

    // ------- Hiking místa z TrailAPI -------

    /**
     * Načte hiking místa z TrailAPI.
     *
     * Pro jednoduchost používáme natvrdo souřadnice někde u San Francisca.
     * Můžeš si je klidně změnit na jinou oblast.
     */
    suspend fun getAreas(): List<ClimbingAreaDto> {
        // souřadnice – můžeš změnit na co chceš
        val lat = 37.7749
        val lon = -122.4194

        val raw: Map<String, TrailPlaceRaw> = api.getHikingPlaces(
            lat = lat,
            lon = lon,
            radius = 50,
            limit = 20,
            state = "California"
        )

        return raw.map { (id, place) ->
            val location = listOfNotNull(
                place.city,
                place.state,
                place.country
            ).joinToString(", ")

            ClimbingAreaDto(
                id = id,
                name = place.name ?: "Unknown trail",
                location = location,
                description = place.description
            )
        }
    }
}
