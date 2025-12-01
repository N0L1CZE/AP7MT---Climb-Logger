package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class ClimbSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // např. "2025-11-27"
    val locationName: String,  // název stěny/oblasti
    val isOutdoor: Boolean
)
