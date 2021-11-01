package rs.dk150.cryptotracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.data.Repository

class CryptoViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    val fetchCrCsResult: MutableLiveData<SingleEvent<Result<CryptoCurrencyList?>?>> =
        MutableLiveData()

    fun fetchCrCs() {
        // can be launched in a separate asynchronous job
        viewModelScope.launch {
            fetchCrCsResult.value = SingleEvent(repository.fetchCrCs())
        }
    }
}