import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.time.LocalTime

fun main() {
    val client = OkHttpClient()
    val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    val jsonBody = """
        {
            "count": 10000, 
            "availableTaps": 3000000, 
            "timestamp": 17213071500
        }
    """.trimIndent()

    val request = Request.Builder()
        .url("https://api.hamsterkombatgame.io/clicker/tap")
        .post(jsonBody.toRequestBody(jsonMediaType))
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", Auth)
        .build()

    val executor = Executors.newSingleThreadScheduledExecutor()

    val task = Runnable {
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                println(response.code)
            }
            else{
                val jsonObject = Json.parseToJsonElement(response.body!!.string()).jsonObject
                val jsonObject1 = Json.parseToJsonElement(jsonObject["clickerUser"].toString()).jsonObject
                println("${LocalTime.now()} Баланс: ${jsonObject1["balanceCoins"]}")
            }
            response.close()
        } catch (e: IOException) {
            println(e)
        }
    }

    executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.MINUTES)
}
