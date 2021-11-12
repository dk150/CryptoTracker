@file:Suppress("JoinDeclarationAndAssignment")

package rs.dk150.cryptotracker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that requests information from data source
 * TODO bonus: and maintains an in-memory cache of that information
 */

object Repository {
    private var dataSource: WebDataSource

    init {
        /* TODO bonus: check whether there is internet connection and consequentially use appropriate DataSource class */
        dataSource = WebDataSource()
    }

    suspend fun fetchCrCs(): Result<CryptoCurrencyList?> {
        // change context of coroutine to an IO thread
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCs()
        }
    }

    suspend fun fetchCrCsDetails(symbol: String): Result<CryptoCurrencyList?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCsDetails(symbol)
        }
    }

    suspend fun fetchCrCsConversion(symbol: String, symbols: String): Result<CurrencyConversion?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCsConversion(symbol, symbols)
        }
    }

    suspend fun fetchCrCsHistoricalHResult(symbol: String): Result<HistoricalList?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCsHistoricalH(symbol)
        }
    }

    suspend fun fetchCrCsHistoricalMResult(symbol: String): Result<HistoricalList?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCsHistoricalM(symbol)
        }
    }

    suspend fun fetchCrCsHistoricalDResult(symbol: String): Result<HistoricalList?> {
        return withContext(Dispatchers.IO) {
            dataSource.fetchCrCsHistoricalD(symbol)
        }
    }

}