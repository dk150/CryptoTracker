package rs.dk150.cryptotracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.data.CurrencyConversion
import rs.dk150.cryptotracker.data.Repository

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


    fun fetchCrCs() {
        // can be launched in a separate asynchronous job
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
}