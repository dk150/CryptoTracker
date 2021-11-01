package rs.dk150.cryptotracker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rs.dk150.cryptotracker.data.Repository

/**
 * ViewModel provider factory to instantiate ViewModel.
 * Required given ViewModel has a non-empty constructor
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(
                Repository, application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}