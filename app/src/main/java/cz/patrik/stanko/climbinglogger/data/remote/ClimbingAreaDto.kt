package cz.patrik.stanko.climbinglogger.data.remote

/**
 * "Raw" odpověď jednoho místa z TrailAPI.
 * Klíče (name, city, state, country, description) vychází ze sample response.
 */
data class TrailPlaceRaw(
    val name: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val description: String?
)

/**
 * DTO, které používá UI.
 * id: string (TrailAPI vrací mapu id -> objekt)
 * location: složené z city, state, country
 */
data class ClimbingAreaDto(
    val id: String,
    val name: String,
    val location: String,
    val description: String?
)
