package rs.dk150.cryptotracker.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rs.dk150.cryptotracker.data.*

/**
 * MVVM arch: ViewModel acts as mediator and allows separation of presentation layer
 * (UI classes) and model layer (data classes); Also ViewModel survives configuration
 * change such as screen-rotation, so there's no need to implement custom stateRestoration
 * for fragments & activity
 */
class CryptoViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    val crCsResult: MutableLiveData<SingleEvent<Result<CryptoCurrencyList?>?>> =
        MutableLiveData()

    val crCsDetailsResult: MutableLiveData<Result<CryptoCurrencyList?>?> =
        MutableLiveData()

    var crCsConversionResult: Result<CurrencyConversion?>? =
        null

    val conversionCurrencies: List<String> by lazy {
        CurrencyConversion.conversionCurrencies()
    }

    val crCsHistoricalHResult: MutableLiveData<Result<HistoricalList?>?> =
        MutableLiveData()
    val crCsHistoricalMResult: MutableLiveData<Result<HistoricalList?>?> =
        MutableLiveData()
    val crCsHistoricalDResult: MutableLiveData<Result<HistoricalList?>?> =
        MutableLiveData()
    /* we do not clear liveData for historicalResult, but we need to remember
    * for which symbol we fetched the result
    */
    var crCsHistoricalSymbolH: String? = null
    var crCsHistoricalSymbolM: String? = null
    var crCsHistoricalSymbolD: String? = null


    fun fetchCrCs() {
        // create a coroutine scope to call suspending function
        viewModelScope.launch {
            crCsResult.value = SingleEvent(repository.fetchCrCs())
        }
    }

    fun fetchCrCsDetails(position: Int) {
        val symbol = crCsResult.value?.getContent()?.getOrDefault(null)?.data?.values?.elementAtOrNull(position)?.symbol?.trim()
        symbol?.let{
            viewModelScope.launch {
                fetchCrCsConversion(it)
                crCsDetailsResult.value = repository.fetchCrCsDetails(it)
            }
        }
    }

    private suspend fun fetchCrCsConversion(symbol: String) {
        crCsConversionResult = repository.fetchCrCsConversion(symbol, conversionCurrencies.joinToString(","))
    }

    fun getSymbol(position: Int) =
        crCsResult.value?.getContent()?.getOrDefault(null)?.data?.values?.elementAtOrNull(position)?.symbol

    fun fetchCrCsHistoricalH(position: Int) {
        val symbol = crCsResult.value?.getContent()?.getOrDefault(null)?.data?.values?.elementAtOrNull(position)?.symbol?.trim()
        symbol?.let{
            viewModelScope.launch {
                crCsHistoricalHResult.value = repository.fetchCrCsHistoricalHResult(it)
                crCsHistoricalSymbolH = symbol
            }
        }
    }

    fun fetchCrCsHistoricalM(position: Int) {
        val symbol = crCsResult.value?.getContent()?.getOrDefault(null)?.data?.values?.elementAtOrNull(position)?.symbol?.trim()
        symbol?.let{
            viewModelScope.launch {
                crCsHistoricalMResult.value = repository.fetchCrCsHistoricalMResult(it)
                crCsHistoricalSymbolM = symbol
            }
        }
    }

    fun fetchCrCsHistoricalD(position: Int) {
        val symbol = crCsResult.value?.getContent()?.getOrDefault(null)?.data?.values?.elementAtOrNull(position)?.symbol?.trim()
        symbol?.let{
            viewModelScope.launch {
                crCsHistoricalDResult.value = repository.fetchCrCsHistoricalDResult(it)
                crCsHistoricalSymbolD = symbol
            }
        }
    }
}