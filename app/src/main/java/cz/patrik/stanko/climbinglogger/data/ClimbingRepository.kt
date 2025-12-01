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
 * Repository – lokální DB (Room) + TrailAPI.
 */
class ClimbingRepository(
    private val sessionDao: SessionDao,
    private val routeDao: RouteDao,
    private val api: ClimbingApiService
) {

    // ------- Sessions (hiking log) -------

    fun getSessions(): Flow<List<ClimbSession>> = sessionDao.getAllSessions()

    suspend fun getSession(id: Long): ClimbSession? =
        sessionDao.getSessionById(id)

    suspend fun addSession(
        title: String,
        date: String,
        durationMinutes: Int?,
        notes: String?,
        photoUri: String?,
        locationName: String?
    ): Long {
        val session = ClimbSession(
            title = title,
            date = date,
            durationMinutes = durationMinutes,
            notes = notes,
            photoUri = photoUri,
            locationName = locationName
        )
        return sessionDao.insertSession(session)
    }

    suspend fun updateSession(session: ClimbSession) {
        sessionDao.updateSession(session)
    }

    suspend fun deleteSession(session: ClimbSession) {
        sessionDao.deleteSession(session)
    }

    // ------- Routes (pokud je chceš používat) -------

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
     * Načte hiking místa z TrailAPI podle zadaných souřadnic.
     *
     * @param lat  šířka
     * @param lon  délka
     * @param state např. "California" (může být null)
     */
    suspend fun getAreas(
        lat: Double,
        lon: Double,
        state: String?
    ): List<ClimbingAreaDto> {

        val raw: Map<String, TrailPlaceRaw> = api.getHikingPlaces(
            lat = lat,
            lon = lon,
            radius = 50,
            limit = 20,
            state = state
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
