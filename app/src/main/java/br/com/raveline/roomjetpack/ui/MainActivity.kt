package br.com.raveline.roomjetpack.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.raveline.roomjetpack.R
import br.com.raveline.roomjetpack.adapter.ItemAdapterMain
import br.com.raveline.roomjetpack.database.SubscriberDatabase
import br.com.raveline.roomjetpack.database.entity.Subscriber
import br.com.raveline.roomjetpack.database.repository.SubscriberRepository
import br.com.raveline.roomjetpack.databinding.ActivityMainBinding
import br.com.raveline.roomjetpack.viewmodel.SubscriberViewModel
import br.com.raveline.roomjetpack.viewmodel.SubscriberViewModelFactory
import com.google.android.material.snackbar.Snackbar

open class MainActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: ItemAdapterMain
    val KEY_REPLY = "KEY_REPLY"

    private var notificationManager: NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //notification
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val db = SubscriberDatabase.getInstance(this@MainActivity)
        val repository = SubscriberRepository(db.subscriberDAO)
        val factory = SubscriberViewModelFactory(repository)

        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)

        dataBinding.viewModel = subscriberViewModel
        dataBinding.lifecycleOwner = this //use live data

        val linearLayoutManager = LinearLayoutManager(this)

        dataBinding.recyclerMainId.layoutManager = linearLayoutManager

        dataBinding.recyclerMainId.setHasFixedSize(true)

        adapter =
            ItemAdapterMain(this) { selectedItem: Subscriber -> listItemClicked(selectedItem) }

        dataBinding.recyclerMainId.adapter = adapter

        displaySubscribersList()

        //notification
        createNotificationChannel(channelId, channelName, channelDescription)

        subscriberViewModel.message.observe(this, { event ->
            event.getContentIfNotHandled()?.let {
                showSnackBar(it)
                dataBinding.root.clearFocus()
                if (it.contains("Success")) {
                    displayNotification(it)
                }
            }
        })

    }


    private fun displaySubscribersList() {

        subscriberViewModel.subscribers.observe(this, { subs ->
            adapter.setList(subs)
            adapter.notifyDataSetChanged()
        })

    }


    private fun showSnackBar(msg: String) {
        if (!msg.contains("filled")) {
            Snackbar.make(dataBinding.root, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#FFA500"))
                .setTextColor(Color.BLACK)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
        } else {
            Snackbar.make(
                dataBinding.root,
                msg,
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.RED)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
        }
    }

    private fun listItemClicked(subscriber: Subscriber) {
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }

    private fun displayNotification(msg: String) {
        val notificationId = 45
        val tapResultIntent = Intent(this@MainActivity, OtherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, tapResultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        //action button 1
        val firstIntent = Intent(this@MainActivity, ItemDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
        }
        val firstPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 15, firstIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val actionButton: NotificationCompat.Action =
            NotificationCompat.Action.Builder(R.drawable.ic_leaf, "Details", firstPendingIntent)
                .build()

        //action button 2
        val secondIntent = Intent(this@MainActivity, ItemDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
        }
        val secondPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 15, secondIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val actionButton2: NotificationCompat.Action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_baseline_sports_volleyball_24,
                "Volleyball",
                secondPendingIntent
            ).build()


        //reply action
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_REPLY).run {
            setLabel("Insert your name here")
            build()
        }

        val replyAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_sports_volleyball_24,
            "Reply",
            pendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        val notification = NotificationCompat.Builder(this@MainActivity, channelId).apply {
            setContentTitle("Notification Room")
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_baseline_emoji_events_24)
                .setAutoCancel(true)
                .addAction(actionButton)
                .addAction(actionButton2)
                .addAction(replyAction)
                .setContentIntent(pendingIntent)
                .priority = NotificationCompat.PRIORITY_HIGH
        }.build()

        notificationManager?.notify(notificationId, notification)
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }

            notificationManager?.createNotificationChannel(channel)

        }
    }

    companion object {
        const val channelId = "br.com.raveline.roomjetpack"
        const val channelDescription = "Room Demo Notification Jetpack 11"
        const val channelName = "RoomNotification"
    }


}