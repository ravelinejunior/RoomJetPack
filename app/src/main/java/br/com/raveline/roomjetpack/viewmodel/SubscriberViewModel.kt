package br.com.raveline.roomjetpack.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.raveline.roomjetpack.database.entity.Subscriber
import br.com.raveline.roomjetpack.database.repository.SubscriberRepository
import br.com.raveline.roomjetpack.utils.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    //create a variable to get all users on db using the factory
    val subscribers = repository.subscribers

    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String?>()
    val inputEmail = MutableLiveData<String?>()
    val inputPeculiarities = MutableLiveData<String?>()

    val saveOrUpdateTextButton = MutableLiveData<String>()
    val clearAllTextButton = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        inputName.value = ""
        inputEmail.value = ""
        inputPeculiarities.value = ""

        saveOrUpdateTextButton.value = "Save"
        clearAllTextButton.value = "Clear All"
    }

    fun saveOrUpdate(): Boolean {

        if (isUpdateOrDelete) {
            if ((inputName.value!!.isNotEmpty() &&
                        inputEmail.value!!.isNotEmpty())
                && Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()
                && inputPeculiarities.value!!.isEmpty()
            ) {

                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                subscriberToUpdateOrDelete.peculiarity = "Regular Bitch!"

                update(subscriberToUpdateOrDelete)

                inputName.value = null
                inputEmail.value = null
                inputPeculiarities.value = null

                isUpdateOrDelete = false

                saveOrUpdateTextButton.value = "Save"
                clearAllTextButton.value = "Clear All"

                statusMessage.value = Event("Updated With Success!")

                return true

            } else if ((inputName.value!!.isNotEmpty() &&
                        inputEmail.value!!.isNotEmpty())
                && Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()
                && inputPeculiarities.value!!.isNotEmpty()
            ) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                subscriberToUpdateOrDelete.peculiarity = inputPeculiarities.value!!

                update(subscriberToUpdateOrDelete)

                inputName.value = null
                inputEmail.value = null
                inputPeculiarities.value = null

                isUpdateOrDelete = false

                saveOrUpdateTextButton.value = "Save"
                clearAllTextButton.value = "Clear All"

                statusMessage.value = Event("Updated With Success!")


                return true
            } else {
                statusMessage.value = Event("All fields must be filled to update this subscriber.")
                return false
            }
        } else {

            val name: String = inputName.value!!
            val email: String = inputEmail.value!!
            var peculiarities: String = inputPeculiarities.value!!

            if ((name.isNotEmpty()
                        && email.isNotEmpty())
                && Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()
                && peculiarities.isEmpty()
            ) {
                peculiarities = "Regular person."

                insert(Subscriber(0, name, email, peculiarities))
                statusMessage.value = Event("Saved With Success!")

                inputName.value = null
                inputEmail.value = null
                inputPeculiarities.value = null

                return true

            } else if (name.isNotEmpty()
                && email.isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()
                && peculiarities.isNotEmpty()
            ) {
                insert(Subscriber(0, name, email, peculiarities))
                statusMessage.value = Event("Saved With Success!")

                inputName.value = null
                inputEmail.value = null
                inputPeculiarities.value = null

                return true

            } else {
                statusMessage.value = Event("All the fields must be filled!")
                return false
            }
        }

    }


    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {

            delete(subscriberToUpdateOrDelete)

            inputName.value = null
            inputEmail.value = null
            inputPeculiarities.value = null

            isUpdateOrDelete = false

            saveOrUpdateTextButton.value = "Save"
            clearAllTextButton.value = "Clear All"

            statusMessage.value = Event("Deleted With Success!")

        } else {
            deleteAll()
            statusMessage.value = Event("Deleted All With Success!")
        }
    }

    private fun insert(subscriber: Subscriber): Job = viewModelScope.launch {
        val newRowId = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Success inserting a new User")
        } else {
            statusMessage.value = Event("Something went wrong!")
        }

    }

    fun getSubscriber(id: Int): Job = viewModelScope.launch {
        repository.getSubscriber(id)
    }

    private fun update(subscriber: Subscriber): Job = viewModelScope.launch {
        val newRowId = repository.update(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("$newRowId updated Successfully")
        } else {
            statusMessage.value = Event("Something went filled wrong!")
        }

    }

    private fun delete(subscriber: Subscriber): Job = viewModelScope.launch {
        repository.delete(subscriber)

    }

    private fun deleteAll(): Job = viewModelScope.launch {
        repository.deleteAll()

    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        inputPeculiarities.value = subscriber.peculiarity

        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber

        saveOrUpdateTextButton.value = "Update"
        clearAllTextButton.value = "Delete"
    }


}