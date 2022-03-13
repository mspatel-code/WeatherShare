package ca.androiddevelopment.weatherapp.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ca.androiddevelopment.weatherapp.R
import ca.androiddevelopment.weatherapp.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var GET:SharedPreferences
    private lateinit var SET:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityName", "Vancouver")
        edit_city_name.setText(cName)

        viewModel.refreshData(cName!!)

        getLiveData()
        swipe_refresh_layout.setOnRefreshListener {
            ll_data_view.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            var cityName = GET.getString("cityName", cName)
            edit_city_name.setText(cityName)
            viewModel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }

        img_search_city_name.setOnClickListener{
            val cityName = edit_city_name.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
        }
    }

    private fun getLiveData() {
        viewModel.weather_data.observe(this, Observer { data ->
            data?.let {
            ll_data_view.visibility = View.VISIBLE }
            pb_loading.visibility = View.GONE
            tv_degree.text = data.main.temp.toString()
            tv_country_code.text = data.sys.country.toString()
            tv_city_name.text = data.name.toString()
            tv_humidity.text = data.main.humidity.toString()
            tv_speed.text = data.wind.speed.toString()
            tv_lat.text = data.coord.lat.toString()
            tv_lon.text = data.coord.lon.toString()

            Glide.with(this).load("http://openweathermap.org/img/wn/" +data.weather.get(0).icon + "@2x.png")
                .into(img_weather_icon)

        })

        viewModel.weather_load.observe(this, Observer { load->
            load?.let {
                if(it){
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    ll_data_view.visibility = View.GONE
                }else{
                    pb_loading.visibility = View.GONE
                }
            }
        })

        viewModel.weather_error.observe(this, Observer { error ->
            error?.let {
                if(it){
                    tv_error.visibility = View.VISIBLE
                    ll_data_view.visibility = View.GONE
                    pb_loading.visibility = View.GONE
                }else{
                    tv_error.visibility = View.GONE
                }
            }
        })

        share_button.setOnClickListener{
            val message: String = share_message.text.toString()
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,message)
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent,"Share to : "))
        }
        }

    }
