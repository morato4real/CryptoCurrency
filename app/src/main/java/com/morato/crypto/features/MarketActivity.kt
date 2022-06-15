package com.morato.crypto.features

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.morato.crypto.apiManager.ApiManager

import com.morato.crypto.databinding.ActivityMarketBinding
import com.morato.crypto.model.CoinAboutData
import com.morato.crypto.model.CoinAboutItem
import com.morato.crypto.model.CoinsData

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {
    lateinit var binding: ActivityMarketBinding
    val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap : MutableMap<String , CoinAboutItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTopCoinsFromApi()
        binding.moduleWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            initUi()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            },1500)
        }

        getAboutDataFromAssets()
    }
    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun initUi() {

        getNewsFromApi()
        getTopCoinsFromApi()
    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()

            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "-> Error $errorMessage", Toast.LENGTH_SHORT)
                    .show()
                Log.v("test", errorMessage.toString())
            }

        })
    }
    private fun refreshNews() {
        val randomAccess = (0..49).random()
        binding.moduleNews.txtNews.text = dataNews[randomAccess].first
        binding.moduleNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }
        binding.moduleNews.txtNews.setOnClickListener { refreshNews() }
    }

    private fun getTopCoinsFromApi() {
        apiManager.getCoinsList(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "error => $errorMessage", Toast.LENGTH_SHORT)
                    .show()
                Log.v("testLog", errorMessage)
            }

        })
    }
    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        val marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.moduleWatchlist.recyclerMain.adapter = marketAdapter
        binding.moduleWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
    }
    override fun coinItemClicked(dataCoin: CoinsData.Data) {
        val intent = Intent(this, CoinActivity::class.java)

        val bundle = Bundle()
        bundle.putParcelable("bundle1" , dataCoin)
        bundle.putParcelable("bundle2" , aboutDataMap[dataCoin.coinInfo.name])
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        aboutDataMap = mutableMapOf<String , CoinAboutItem>()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString , CoinAboutData::class.java)
        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web ,
                it.info.github ,
                it.info.twt ,
                it.info.desc ,
                it.info.reddit
            )
        }


    }
}