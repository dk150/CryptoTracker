package rs.dk150.cryptotracker.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

/**
 * POJO mapping currency conversion response
 */
class CurrencyConversion: ResponsePOJO {
    @SerializedName("Response")
    @Expose
    override val response: String? = null

    @SerializedName("Message")
    @Expose
    override val message: String? = null

    @SerializedName("BTC")
    @Expose
    @Pos(1)
    val btc: Double? = null

    @SerializedName("ETH")
    @Expose
    @Pos(2)
    val eth: Double? = null

    @SerializedName("EVN")
    @Expose
    @Pos(3)
    val evn: Double? = null

    @SerializedName("DOGE")
    @Expose
    @Pos(4)
    val doge: Double? = null

    @SerializedName("ZEC")
    @Expose
    @Pos(5)
    val zec: Double? = null

    @SerializedName("USD")
    @Expose
    @Pos(6)
    val usd: Double? = null

    @SerializedName("EUR")
    @Expose
    @Pos(7)
    val eur: Double? = null

    companion object {
        /* get field names through reflection */
        @JvmStatic
        fun conversionCurrencies(): List<String> {
            return CurrencyConversion::class.memberProperties.filter {
                it.hasAnnotation<Pos>()
            }.sortedBy {
                (it.annotations.find { annotation -> annotation is Pos } as Pos).value
            }.map { it.name.uppercase() }
        }
    }
}