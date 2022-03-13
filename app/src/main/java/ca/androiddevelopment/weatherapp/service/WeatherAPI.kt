package ca.androiddevelopment.weatherapp.service
import ca.androiddevelopment.weatherapp.model.WeatherModel
import retrofit2.http.GET
import io.reactivex.Single
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q=vancouver&appid=f445357558b564714d3fe10f5ec0376f
interface WeatherAPI {
    @GET("data/2.5/weather?&units=metric&appid=f445357558b564714d3fe10f5ec0376f")

    fun getData(
        @Query("q") cityName: String
    ): Single<WeatherModel>
}