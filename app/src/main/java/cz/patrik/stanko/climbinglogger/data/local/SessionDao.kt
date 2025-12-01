package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<ClimbSession>>

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): ClimbSession?

    @Insert
    suspend fun insertSession(session: ClimbSession): Long

    @Update
    suspend fun updateSession(session: ClimbSession)

    @Delete
    suspend fun deleteSession(session: ClimbSession)
}
