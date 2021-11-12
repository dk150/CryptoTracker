package rs.dk150.cryptotracker.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rs.dk150.cryptotracker.BuildConfig

/**
 * Retrofit interface defining REST calls
 */
interface APIInterface {
    companion object {
        /* this value is defined in local.properties file excluded from git tracking & made
           available for use in code through secrets gradle plugin
           https://github.com/google/secrets-gradle-plugin */
        private const val api_key = BuildConfig.CRYPTOCOMPARE_API_KEY
    }

    // all coins list summary call
    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/all/coinlist")
    fun getCrCs(@Query("summary") onlyBasicInfo: Boolean = true): Call<CryptoCurrencyList>

    // all data for coin from all coins list call
    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/all/coinlist")
    fun getCrCsDetails(@Query("fsym") symbol: String): Call<CryptoCurrencyList>

    // currency conversion call
    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/price")
    fun getCrCsConversion(@Query("fsym") symbol: String, @Query("tsyms") symbols: String): Call<CurrencyConversion>

    // historical price value calls
    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/v2/histominute")
    fun getCrCsHistoricalM(@Query("fsym") symbol: String, @Query("tsym") symbolIn: String, @Query("limit") limit: Int, @Query("tryConversion") tryConversion: Boolean = false): Call<HistoricalList>

    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/v2/histohour")
    fun getCrCsHistoricalH(@Query("fsym") symbol: String, @Query("tsym") symbolIn: String, @Query("limit") limit: Int, @Query("tryConversion") tryConversion: Boolean = false): Call<HistoricalList>

    @Headers("authorization: Apikey {${api_key}}")
    @GET("/data/v2/histoday")
    fun getCrCsHistoricalD(@Query("fsym") symbol: String, @Query("tsym") symbolIn: String, @Query("limit") limit: Int, @Query("tryConversion") tryConversion: Boolean = false): Call<HistoricalList>
}