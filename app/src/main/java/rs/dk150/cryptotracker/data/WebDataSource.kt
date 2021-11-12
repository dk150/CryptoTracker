package rs.dk150.cryptotracker.data

import retrofit2.Response

/**
 * Class that retrieves crypto information from Web
 */
class WebDataSource {
    private var apiService = APIClient.client?.create(APIInterface::class.java)


    fun fetchCrCs(): Result<CryptoCurrencyList?> {
        return try {
            val response: Response<CryptoCurrencyList?> =
                apiService!!.getCrCs().execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsDetails(symbol: String): Result<CryptoCurrencyList?> {
        return try {
            val response: Response<CryptoCurrencyList?> =
                apiService!!.getCrCsDetails(symbol).execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsConversion(symbol: String, symbols: String): Result<CurrencyConversion?> {
        return try {
            val response: Response<CurrencyConversion?> =
                apiService!!.getCrCsConversion(symbol, symbols).execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsHistoricalH(symbol: String): Result<HistoricalList?> {
        return try {
            val response: Response<HistoricalList?> =
                apiService!!.getCrCsHistoricalH(symbol, "BTC", 168).execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsHistoricalM(symbol: String): Result<HistoricalList?> {
        return try {
            val response: Response<HistoricalList?> =
                apiService!!.getCrCsHistoricalM(symbol, "BTC", 1440).execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsHistoricalD(symbol: String): Result<HistoricalList?> {
        return try {
            val response: Response<HistoricalList?> =
                apiService!!.getCrCsHistoricalD(symbol, "BTC", 30).execute()
            checkResponse(response)
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    /* check if response is successful */
    private fun <T : ResponsePOJO> checkResponse(response: Response<T?>): Result<T?> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (body.response.equals("Error")) {
                    Result.failure(Throwable("WEB ERROR: ${body.message}"))
                } else {
                    Result.success(body)
                }
            } else {
                Result.failure(Throwable("WEB ERROR"))
            }
        } else {
            Result.failure(Throwable("WEB ERROR: ${response.message()}"))
        }
    }
}