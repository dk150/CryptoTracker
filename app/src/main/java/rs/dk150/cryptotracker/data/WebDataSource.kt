package rs.dk150.cryptotracker.data

import retrofit2.Response

/**
 * Class that retrieves crypto information from Web.
 */
class WebDataSource {
    private var apiService = APIClient.client?.create(APIInterface::class.java)


    fun fetchCrCs(): Result<CryptoCurrencyList?> {
        return try {
            val response: Response<CryptoCurrencyList?> =
                apiService!!.getCrCs().execute()
            if (response.isSuccessful) {
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
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }


    fun fetchCrCsDetails(symbol: String): Result<CryptoCurrencyList?> {
        return try {
            val response: Response<CryptoCurrencyList?> =
                apiService!!.getCrCsDetails(symbol).execute()
            if (response.isSuccessful) {
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
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }

    fun fetchCrCsConversion(symbol: String, symbols: String): Result<CurrencyConversion?> {
        return try {
            val response: Response<CurrencyConversion?> =
                apiService!!.getCrCsConversion(symbol, symbols).execute()
            if (response.isSuccessful) {
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
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB ERROR: Check Internet Connection!"))
        }
    }
}