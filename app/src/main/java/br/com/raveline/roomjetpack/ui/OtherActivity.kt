package br.com.raveline.roomjetpack.ui

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.databinding.DataBindingUtil
import br.com.raveline.roomjetpack.R
import br.com.raveline.roomjetpack.databinding.ActivityOtherBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class OtherActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityOtherBinding
    val KEY_REPLY = "KEY_REPLY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_other)
        setSupportActionBar(findViewById(R.id.toolbar))

        receiveInput()

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Um FloatingActionButton", Snackbar.LENGTH_LONG)
                .setAction("Okay", null).show()
        }
    }

    private fun receiveInput() {
        val intent = this.intent
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            val inputString = remoteInput.getCharSequence(KEY_REPLY).toString()
            dataBinding.tvOtherActivityId.text = inputString

            val notificationId = 45

            val repliedNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_baseline_sports_volleyball_24)
                .setContentText("Your reply received!")
                .build()

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, repliedNotification)
        }
    }

    companion object {
        const val channelId = "br.com.raveline.roomjetpack"
        const val channelDescription = "Room Demo Notification Jetpack 11"
        const val channelName = "RoomNotification"
    }
}