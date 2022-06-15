package com.morato.crypto.features

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.morato.crypto.R
import com.morato.crypto.apiManager.ApiManager

import com.morato.crypto.databinding.ActivityCoinBinding
import com.morato.crypto.model.*

class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    lateinit var thisCoinData: CoinsData.Data
    lateinit var dataThisCoinAbout: CoinAboutItem
    val apiManager = ApiManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fromIntent = intent.getBundleExtra("bundle")!!
        thisCoinData = fromIntent.getParcelable<CoinsData.Data>("bundle1")!!

        if (fromIntent.getParcelable<CoinAboutItem>("bundle2") != null) {
            dataThisCoinAbout = fromIntent.getParcelable<CoinAboutItem>("bundle2")!!
        } else {
            dataThisCoinAbout = CoinAboutItem()
        }



        binding.moduleToolbar.toolbar.title = thisCoinData.coinInfo.fullName
        initUi()

    }

    private fun initUi() {
        initChartUi()
        initStatisticUi()
        initAboutUi()
    }

    private fun initAboutUi() {

        binding.layoutAbout.website.text = dataThisCoinAbout.coinWebsite
        binding.layoutAbout.github.text = dataThisCoinAbout.coinGithub
        binding.layoutAbout.twitter.text = dataThisCoinAbout.coinTwitter
        binding.layoutAbout.reddit.text = dataThisCoinAbout.coinReddit
        binding.layoutAbout.txtAboutCoin.text = dataThisCoinAbout.coinDesc


        binding.layoutAbout.website.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!) }
        binding.layoutAbout.github.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!) }
        binding.layoutAbout.twitter.setOnClickListener { openWebsiteDataCoin(BASE_URL_TWITTER + dataThisCoinAbout.coinWebsite!!) }
        binding.layoutAbout.reddit.setOnClickListener { openWebsiteDataCoin(dataThisCoinAbout.coinWebsite!!) }


    }

    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun initStatisticUi() {

        binding.layoutStatistics.tvOpenAmount.text = thisCoinData.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvTodaysHighAmount.text = thisCoinData.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvTodayLowAmount.text = thisCoinData.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvChangeTodayAmount.text = thisCoinData.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvAlgorithm.text = thisCoinData.dISPLAY.uSD.vOLUME24HOUR
        binding.layoutStatistics.tvTotalVolume.text = thisCoinData.dISPLAY.uSD.tOTALVOLUME24H
        binding.layoutStatistics.tvAvgMarketCapAmount.text = thisCoinData.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.tvSupplyNumber.text = thisCoinData.dISPLAY.uSD.sUPPLY
    }

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {

        var period: String = HOUR
        requestAndShowChart(period)

        binding.layoutChart.radioGroupMain.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio12h -> {
                    period = HOUR
                }
                R.id.radio1D -> {
                    period = HOURS24
                }
                R.id.radio1W -> {
                    period = WEEK
                }
                R.id.radio1M -> {
                    period = MONTH
                }
                R.id.radio3M -> {
                    period = MONTH3
                }
                R.id.radio1Y -> {
                    period = YEAR
                }
                R.id.radioAll -> {
                    period = ALL
                }
            }
            requestAndShowChart(period)
        }

        binding.layoutChart.txtChartPrice.text = thisCoinData.dISPLAY.uSD.pRICE
        binding.layoutChart.txtChartChange1.text = thisCoinData.dISPLAY.uSD.cHANGE24HOUR

        if (thisCoinData.coinInfo.fullName == "BUSD") {
            binding.layoutChart.txtChartChange2.text = "0%"

        } else {
            binding.layoutChart.txtChartChange2.text =
                thisCoinData.rAW.uSD.cHANGEPCT24HOUR.toString().substring(0, 5) + "%"
        }

        val taghir = thisCoinData.rAW.uSD.cHANGE24HOUR

        if (taghir > 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )

            binding.layoutChart.txtChartUpDown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorGain
                )
            )

            binding.layoutChart.txtChartUpDown.text = "▲"

            binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(
                binding.root.context,
                R.color.colorGain
            )

        } else if (taghir < 0) {
            binding.layoutChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )

            binding.layoutChart.txtChartUpDown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.colorLoss
                )
            )

            binding.layoutChart.txtChartUpDown.text = "▼"


            binding.layoutChart.sparkViewMain.lineColor = ContextCompat.getColor(
                binding.root.context,
                R.color.colorLoss
            )

        }

        binding.layoutChart.sparkViewMain.setScrubListener {
            if (it == null){
                binding.layoutChart.txtChartPrice.text = thisCoinData.dISPLAY.uSD.pRICE
            }else{
                binding.layoutChart.txtChartPrice.text = "$" + (it as ChartData.Data).close.toString()
            }
        }
    }


    private fun requestAndShowChart(period: String) {
        apiManager.getChartData(
            thisCoinData.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {

                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkViewMain.adapter = chartAdapter
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@CoinActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }

            })
    }
}