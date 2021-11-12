package rs.dk150.cryptotracker.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * POJO mapping historical price value response
 */
class HistoricalList: ResponsePOJO {
    @SerializedName("Response")
    @Expose
    override val response : String? = null
    @SerializedName("Message")
    @Expose
    override val message : String? = null
    @SerializedName("Data")
    @Expose
    val data: Data? = null

    /** POJO mapping data from historical price value response */
    class Data {
        @SerializedName("Data")
        @Expose
        val data: List<Value>? = null
    }

    /** POJO mapping one price value in time point from historical price value response */
    class Value {
        @SerializedName("close")
        @Expose
        val close: Float? = null

        @SerializedName("time")
        @Expose
        val time: Long? = null
    }
}
