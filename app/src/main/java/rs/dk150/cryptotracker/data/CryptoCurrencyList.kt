package rs.dk150.cryptotracker.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CryptoCurrencyList {
    @SerializedName("Response")
    @Expose
    val response : String? = null
    @SerializedName("Message")
    @Expose
    val message : String? = null
    @SerializedName("Data")
    @Expose
    val data: Map<String, CryptoCurrency>? = null
}
