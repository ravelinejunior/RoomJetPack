package br.com.raveline.roomjetpack.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.raveline.roomjetpack.database.SubscriberDatabase.Companion.VERSION_DATABASE
import br.com.raveline.roomjetpack.database.dao.SubscriberDAO
import br.com.raveline.roomjetpack.database.entity.Subscriber


@Database(entities = [Subscriber::class], version = VERSION_DATABASE ,exportSchema = false)
abstract class SubscriberDatabase : RoomDatabase() {

    abstract val subscriberDAO: SubscriberDAO

    companion object {
        const val VERSION_DATABASE: Int = 1
        private const val SUBSCRIBER_DB_NAME: String = "db_subscriber"

        //singleton para instancia de banco de dados (volatile para outras threads)
        @Volatile
        private var DB_INSTANCE: SubscriberDatabase? = null
        fun getInstance(context: Context): SubscriberDatabase {
            synchronized(this) {
                var instance = DB_INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        SubscriberDatabase::class.java,
                        SUBSCRIBER_DB_NAME
                    ).build()
                }
                return instance
            }
        }
    }
}