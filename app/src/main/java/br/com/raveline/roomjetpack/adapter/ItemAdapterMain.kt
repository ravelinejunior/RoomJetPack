package br.com.raveline.roomjetpack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.raveline.roomjetpack.R
import br.com.raveline.roomjetpack.database.entity.Subscriber
import br.com.raveline.roomjetpack.databinding.ItemAdapterMainBinding


class ItemAdapterMain(
    private val context: Context,
    private val clickListener: (Subscriber)->Unit
) :
    RecyclerView.Adapter<ItemAdapterMain.MyViewHolder>() {

    private val subscriberList= ArrayList<Subscriber>()

    class MyViewHolder(private val dataBinding: ItemAdapterMainBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {


        fun bind(subscriber: Subscriber, clickListener: (Subscriber)->Unit) {
            dataBinding.tvNameAdapterId.text = subscriber.name
            dataBinding.tvEmailAdapterId.text = subscriber.email
            dataBinding.tvPeculiaritiesAdapterId.text = subscriber.peculiarity

            dataBinding.cardItemAdapterMain.setOnClickListener {
                clickListener(subscriber)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(context)

        val dataBinding: ItemAdapterMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_adapter_main, parent, false)

        return MyViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscriberList[position],clickListener )
    }

    override fun getItemCount(): Int = subscriberList.size

    fun setList(subscribers: List<Subscriber>){
        subscriberList.clear()
        subscriberList.addAll(subscribers)

    }



}