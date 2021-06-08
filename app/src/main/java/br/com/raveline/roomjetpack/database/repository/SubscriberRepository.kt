package br.com.raveline.roomjetpack.database.repository

import br.com.raveline.roomjetpack.database.dao.SubscriberDAO
import br.com.raveline.roomjetpack.database.entity.Subscriber

class SubscriberRepository(private val dao: SubscriberDAO) {
    val subscribers = dao.getAllSubscribers()

    suspend fun getSubscriber(id: Int) {
        dao.getSubscriber(id)
    }

    suspend fun insert(subscriber: Subscriber) :Long{
       return  dao.insertSubscriber(subscriber)
    }

    suspend fun update(subscriber: Subscriber):Int {
        return dao.updateSubscriber(subscriber)
    }

    suspend fun delete(subscriber: Subscriber) {
        dao.deleteSubscriber(subscriber)
    }

    suspend fun deleteAll() {
        dao.deleteAllSubscribers()
    }


}