package rs.dk150.cryptotracker.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CryptoCurrency {
    // basic attributes
    @SerializedName("Id")
    @Expose
    val id: String? = null

    @SerializedName("FullName")
    @Expose
    val fullName: String? = null

    @SerializedName("Symbol")
    @Expose
    val symbol: String? = null

    @SerializedName("ImageUrl")
    @Expose
    val imageUrl: String? = null

    // additional attributes
    @SerializedName("CoinName")
    @Expose
    val coinName: String? = null

    @SerializedName("Name")
    @Expose
    val name: String? = null

    @SerializedName("Description")
    @Expose
    val description: String? = null

    @SerializedName("Algorithm")
    @Expose
    val algorithm: String? = null

    @SerializedName("ProofType")
    @Expose
    val proofType: String? = null

    @SerializedName("TotalCoinsMined")
    @Expose
    val totalCoinsMined: Float? = null

    @SerializedName("CirculatingSupply")
    @Expose
    val circulatingSupply: Float? = null

    @SerializedName("AssetWebsiteUrl")
    @Expose
    val assetWebsiteUrl: String? = null

    /* TODO: parse it as String because there is an error in format of retrieved json; then try to parse it as Date manually
    @SerializedName("AssetLaunchDate")
    @Expose
    private val assetLaunchDate: Date? = null*/
}