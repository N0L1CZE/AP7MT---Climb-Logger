package cz.patrik.stanko.climbinglogger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ClimbSession::class, ClimbRoute::class],
    version = 1
)
abstract class ClimbDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun routeDao(): RouteDao
}
