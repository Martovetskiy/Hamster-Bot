import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.lang.Thread.sleep
import java.time.LocalTime

fun main() {
    val client = OkHttpClient()
    val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    val jsonBodyTap = """
        {
            "count": 10000, 
            "availableTaps": 3000000, 
            "timestamp": 17213071500
        }
    """.trimIndent()

    val jsonBodyBuy = """
        {
            {"boostId": "BoostMaxTaps", "timestamp": 1721303440}
        }
    """.trimIndent()

    var minutesOf: Int

    val tapRequest = Request.Builder()
        .url("https://api.hamsterkombatgame.io/clicker/tap")
        .post(jsonBodyTap.toRequestBody(jsonMediaType))
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", Auth)
        .build()

    val buyRequest = Request.Builder()
        .url("https://api.hamsterkombatgame.io/clicker/buy-boost")
        .post(jsonBodyBuy.toRequestBody(jsonMediaType))
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", Auth)
        .build()

    val executor = Executors.newSingleThreadScheduledExecutor()

    val task = Runnable {
        try {
            var response = client.newCall(tapRequest).execute()
            if (!response.isSuccessful) {
                println(response.code)
            }
            else{
                val jsonObject = Json.parseToJsonElement(response.body!!.string()).jsonObject
                val jsonObject1 = Json.parseToJsonElement(jsonObject["clickerUser"].toString()).jsonObject
                println("${LocalTime.now()} Баланс: ${jsonObject1["balanceCoins"]}")
                minutesOf = jsonObject1["maxTaps"].toString().toInt()/3*1000
                println("Денег за итерацию: ${jsonObject1["maxTaps"]}")
                println("Кулдаун: ${minutesOf/60000} минут")

                response.close()

                response = client.newCall(buyRequest).execute()
                response.close()
                sleep(minutesOf.toLong())
            }
        } catch (e: IOException) {
            println(e)
        }
    }

    executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS)
}
