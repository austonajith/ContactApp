package list.user.listingapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import list.user.listingapp.data.local.dao.RemoteKeysDao
import list.user.listingapp.data.local.dao.UserDao
import list.user.listingapp.data.local.entity.RemoteKeys
import list.user.listingapp.data.local.entity.UserInfoEntity

@Database(entities = [
    UserInfoEntity::class,
    RemoteKeys::class,
], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private const val DATABASE_NAME = "bookshelf"

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): UserDatabase {
            val db = Room.databaseBuilder(
                context.applicationContext,
                UserDatabase::class.java,
                DATABASE_NAME
            )
            return db.build()
        }
    }
}