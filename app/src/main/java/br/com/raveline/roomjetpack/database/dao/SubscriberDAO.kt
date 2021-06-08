package br.com.raveline.roomjetpack.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.raveline.roomjetpack.database.entity.Subscriber

@Dao
interface SubscriberDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriber(subscriber: Subscriber):Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubscriber(subscriber: Subscriber):Int

    @Query("SELECT * FROM subscriber_tb where id = :id")
    fun getSubscriber(id: Int): LiveData<Subscriber>

    @Query("SELECT * FROM SUBSCRIBER_TB")
    fun getAllSubscribers(): LiveData<List<Subscriber>>

    @Delete(entity = Subscriber::class)
    suspend fun deleteSubscriber(subscriber: Subscriber): Int

    @Query("DELETE FROM subscriber_tb")
    suspend fun deleteAllSubscribers()


}