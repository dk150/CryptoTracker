package rs.dk150.cryptotracker.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rs.dk150.cryptotracker.BuildConfig

interface APIInterface {
    companion object {
        private const val api_key = BuildConfig.CRYPTOCOMPARE_API_KEY
    }

    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/all/coinlist")
    fun getCrCs(@Query("summary") onlyBasicInfo: Boolean = true): Call<CryptoCurrencyList>

    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/all/coinlist")
    fun getCrCsDetails(@Query("fsym") symbol: String): Call<CryptoCurrencyList>

    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/price")
    fun getCrCsConversion(@Query("fsym") symbol: String, @Query("tsyms") symbols: String): Call<CurrencyConversion>
}