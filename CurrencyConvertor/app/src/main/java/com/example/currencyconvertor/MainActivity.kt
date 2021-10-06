package com.example.currencyconvertor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
class MainActivity : AppCompatActivity() {
    var currency: Double =0.0
    var selected:Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userinp = findViewById<View>(R.id.userinput) as EditText
        val convrt = findViewById<View>(R.id.btn) as Button
        val spinner = findViewById<View>(R.id.spr) as Spinner
        val cur = arrayListOf("inr", "usd", "aud", "sar", "cny", "jpy")



        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, cur
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    selected = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        convrt.setOnClickListener{
            var sel=userinp.text.toString()
            currency = sel.toDouble()

            requestAPI()


        }

    }
    private fun disp(calc: Double) {

        val responseText = findViewById<View>(R.id.tvCalc) as TextView

        responseText.text = "result " + calc
    }
    private fun calc(i: Double?, sel: Double): Double {
        var s = 0.0
        if (i != null) {
            s = (i * sel)
        }
        return s
    }
    private fun requestAPI() {
        // we use Coroutines to fetch the data, then update the Recycler View if the data is valid
        CoroutineScope(IO).launch {
            // we fetch the prices
            val data = async { fetchPrices() }.await()
            // once the data comes back, we populate our Recycler View
            if (data.isNotEmpty()) {
                    getData(data)
            } else {
                Log.d("MAIN", "Unable to get data")
            }
        }
    }

    private fun fetchPrices():String {
        // we will use URL.readText() to get our data (https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io/java.net.-u-r-l/read-text.html)
        // we make a call to the following API: https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json
        var response = ""
        try {
            response =
                URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json").readText()
        } catch (e: Exception) {
            Log.d("MAIN", "ISSUE: $e")
        }
        // our response is saved as a string
        return response
    }
    private suspend fun getData(result: String){
        withContext(Main){
            // we create a JSON object from the data
            val jsonObj = JSONObject(result)

            // to go deeper, we can use the getString method (here we get the value of USD)

            val usd = jsonObj.getJSONObject("eur").getString("usd")
            val aud = jsonObj.getJSONObject("eur").getString("aud")
            val inr = jsonObj.getJSONObject("eur").getString("inr")
            val sar = jsonObj.getJSONObject("eur").getString("sar")
            val cny = jsonObj.getJSONObject("eur").getString("cny")
            val jpy = jsonObj.getJSONObject("eur").getString("jpy")

            when (selected) {
                0 -> disp(calc(inr.toDouble(),currency));
                1 -> disp(calc(usd.toDouble(),currency));
                2 -> disp(calc(aud.toDouble(),currency));
                3 -> disp(calc(sar.toDouble(),currency));
                4 -> disp(calc(cny.toDouble(),currency));
                5 -> disp(calc(jpy.toDouble(),currency));
            }

        }
    }
}