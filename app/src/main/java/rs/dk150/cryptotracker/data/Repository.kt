package rs.dk150.cryptotracker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that requests information from data source
 * bonus: and maintains an in-memory cache of that information.
 */

object Repository {
    private var dataSource: WebDataSource
    init {
        /* bonus: check whether there is internet connection
           and consequentially use appropriate DataSource class */
        dataSource = WebDataSource()
    }
    suspend fun fetchCrCs(): Result<CryptoCurrencyList?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCs()
        }
    }
}