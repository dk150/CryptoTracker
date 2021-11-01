package rs.dk150.cryptotracker.data

import retrofit2.Response

/**
 * Class that retrieves crypto information from Web.
 */
class WebDataSource {
    private var apiService = APIClient.client?.create(APIInterface::class.java)

    fun fetchCrCs(): Result<CryptoCurrencyList?> {
        return try {
            val response: Response<CryptoCurrencyList> = apiService!!.getCrCs().execute()
            if(response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Throwable("WEB_ERROR: ${response.message()}"))
            }
        } catch (e: Throwable) {
            Result.failure(Throwable("WEB_ERROR: ${e.localizedMessage}"))
        }
    }
}