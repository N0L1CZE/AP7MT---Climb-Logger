package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<ClimbSession>>

    @Insert
    suspend fun insertSession(session: ClimbSession)
}
