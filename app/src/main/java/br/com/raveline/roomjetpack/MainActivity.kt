package br.com.raveline.roomjetpack

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        subscriberViewModel.message.observe(this, { event ->
            event.getContentIfNotHandled()?.let {
                showSnackBar(it)
                dataBinding.root.clearFocus()
                hideKeyboard()
            }
        })

    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val imm: InputMethodManager =
                this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            view?.post {
                imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
                imm.hideSoftInputFromInputMethod(this.currentFocus?.windowToken, 0)
            }
        } else {
            if (view != null) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(dataBinding.root.windowToken, 0)
                imm.hideSoftInputFromInputMethod(dataBinding.root.windowToken, 0)
            }
        }
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


}