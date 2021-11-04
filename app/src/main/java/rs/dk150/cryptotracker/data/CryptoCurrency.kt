package rs.dk150.cryptotracker.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

class CryptoCurrency {
    // basic attributes
    @SerializedName("Id")
    @Expose
    @Pos(1)
    val id: String? = null

    @SerializedName("FullName")
    @Expose
    @Pos(2)
    val fullName: String? = null

    @SerializedName("Symbol")
    @Expose
    @Pos(3)
    val symbol: String? = null

    @SerializedName("ImageUrl")
    @Expose
    @Pos(4)
    val imageUrl: String? = null

    // additional attributes
    @SerializedName("Name")
    @Expose
    @Pos(5)
    val name: String? = null

    @SerializedName("CoinName")
    @Expose
    @Pos(6)
    val coinName: String? = null

    @SerializedName("Description")
    @Expose
    @Pos(7)
    val description: String? = null

    @SerializedName("Url")
    @Expose
    @Pos(8)
    val url: String? = null

    @SerializedName("AssetTokenStatus")
    @Expose
    @Pos(9)
    val assetTokenStatus: String? = null

    @SerializedName("Algorithm")
    @Expose
    @Pos(10)
    val algorithm: String? = null

    @SerializedName("ProofType")
    @Expose
    @Pos(11)
    val proofType: String? = null

    @SerializedName("TotalCoinsMined")
    @Expose
    @Pos(12)
    val totalCoinsMined: Float? = null

    @SerializedName("CirculatingSupply")
    @Expose
    @Pos(13)
    val circulatingSupply: Float? = null

    @SerializedName("BlockNumber")
    @Expose
    @Pos(14)
    val blockNumber: Int? = null

    @SerializedName("NetHashesPerSecond")
    @Expose
    @Pos(15)
    val netHashesPerSecond: Int? = null

    @SerializedName("BlockReward")
    @Expose
    @Pos(16)
    val blockReward: Float? = null

    @SerializedName("BlockTime")
    @Expose
    @Pos(17)
    val blockTime: Int? = null

    @SerializedName("AssetLaunchDate")
    @Expose
    @Pos(18)
    var assetLaunchDate: String? = null

    @SerializedName("AssetWhitepaperUrl")
    @Expose
    @Pos(19)
    val assetWhitepaperUrl: String? = null

    @SerializedName("AssetWebsiteUrl")
    @Expose
    @Pos(20)
    val assetWebsiteUrl: String? = null

    @SerializedName("MaxSupply")
    @Expose
    @Pos(21)
    val maxSupply: Double? = null

    @SerializedName("IsUsedInDefi")
    @Expose
    @Pos(22)
    val isUsedInDefi: Int? = null

    @SerializedName("IsUsedInNft")
    @Expose
    @Pos(23)
    val isUsedInNft: Int? = null

    @SerializedName("PlatformType")
    @Expose
    @Pos(24)
    val platformType: String? = null

    @SerializedName("AlgorithmType")
    @Expose
    @Pos(25)
    val algorithmType: String? = null

    fun getFieldValues(): ArrayList<CryptoField> {
        val fields = CryptoCurrency::class.memberProperties.filter {
            it.hasAnnotation<Pos>() && properValue(it.get(this))
        }.sortedBy {
            (it.annotations.find { annotation -> annotation is Pos } as Pos).value
        }
        val result = ArrayList<CryptoField>(fields.size)
        for ((i, field) in fields.withIndex()) {
            result.add(i, CryptoField(field.name, field.get(this).toString()))
        }
        return result
    }

    private fun <V>properValue(value: V): Boolean {
        return when (value) {
            null -> {
                false
            }
            is String? -> {
                val v = value.trim()
                return if(v.isEmpty()) {
                    false
                } else !(v.matches(Regex.fromLiteral("0000-00-00")) || v.matches(Regex.fromLiteral("N/A")))
            }
            else -> {
                return true
            }
        }
    }
}
