package ltd.zake.yakumochatbot.utils

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import ltd.zake.yakumochatbot.YCPluginMain.YCSetting.weatherAppKey
import ltd.zake.yakumochatbot.data.Weather

class WeatherAPI {
    val httpGettre = HttpGettre()
    val weatherUrl =
        StringBuilder("https://restapi.amap.com/v3/weather/weatherInfo?key=${weatherAppKey}&extensions=base&output=JSON")
    val cityIdUrl = StringBuilder("https://restapi.amap.com/v3/geocode/geo?key=${weatherAppKey}")


    fun getCityId(name: String): String {
        val getUrl = cityIdUrl.append("&address=$name").toString()
        val Json = httpGettre.httpGet(getUrl)
        val parser = Parser.default()
        val stringParser = parser.parse(StringBuilder(Json)) as JsonObject
        val geocodes = stringParser.array<JsonObject>("geocodes") as JsonArray<JsonObject>
        val table = geocodes[0]
        return table.string("adcode")!!
    }

    fun getWeather(city: String): Weather {
        val getUrl = weatherUrl.append("&city=$city")
        val Json = httpGettre.httpGet(getUrl.toString())
        val parser = Parser.default()
        val stringParser = parser.parse(StringBuilder(Json)) as JsonObject
        val geocodes = stringParser.array<JsonObject>("lives") as JsonArray<JsonObject>
        val table = geocodes[0]
        return Weather(
            table.string("province")!!,
            table.string("city")!!,
            table.string("weather")!!,
            table.string("temperature")!!,
            table.string("winddirection")!!,
            table.string("windpower")!!,
            table.string("humidity")!!,
            table.string("reporttime")!!
        )
    }

}