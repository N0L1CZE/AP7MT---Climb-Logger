package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {

    @Query("SELECT * FROM routes WHERE sessionId = :sessionId")
    fun getRoutesForSession(sessionId: Long): Flow<List<ClimbRoute>>

    @Insert
    suspend fun insertRoute(route: ClimbRoute)
}
