package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class ClimbSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val durationMinutes: Int?,
    val notes: String?,
    val photoUri: String?,
    val locationName: String?
)
