package com.morato.crypto.features

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.morato.crypto.R

import com.morato.crypto.databinding.ItemRecyclerMarketBinding
import com.morato.crypto.model.BASE_URL_IMAGE
import com.morato.crypto.model.CoinsData

class MarketAdapter(private val data : ArrayList<CoinsData.Data> ,
                    private val recyclerCallback: RecyclerCallback
                    ) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>(){
    lateinit var binding: ItemRecyclerMarketBinding


    inner class MarketViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){


        @SuppressLint("SetTextI18n")
        fun bindViews(dataCoin : CoinsData.Data){
            binding.txtCoinName.text = dataCoin.coinInfo.fullName
            binding.txtPrice.text = "$" + dataCoin.dISPLAY.uSD.pRICE.toString()

            val marketCap = dataCoin.rAW.uSD.mKTCAP / 1000000000
            val indexDot = marketCap.toString().indexOf('.')
            binding.txtMarketCap.text = "$" + marketCap.toString().substring(0,indexDot+3) + " B"

            val taghir = dataCoin.rAW.uSD.cHANGEPCT24HOUR
            if (taghir > 0){
                binding.txtTaghir.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorGain))
                binding.txtTaghir.text = dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0,4) + "%"
            }else if (taghir < 0){
                binding.txtTaghir.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorLoss))
                binding.txtTaghir.text = dataCoin.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0,5) + "%"
            }else{
                binding.txtTaghir.text = "0%"
            }

            Glide.with(itemView).load(BASE_URL_IMAGE + dataCoin.coinInfo.imageUrl).into(binding.imageItem)

            itemView.setOnClickListener {
                recyclerCallback.coinItemClicked(dataCoin)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        binding = ItemRecyclerMarketBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int = data.size

    interface RecyclerCallback{
        fun coinItemClicked(dataCoin : CoinsData.Data)
    }
}