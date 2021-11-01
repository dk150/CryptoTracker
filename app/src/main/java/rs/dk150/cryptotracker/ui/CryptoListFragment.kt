package rs.dk150.cryptotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.databinding.FragmentCryptoListBinding


class CryptoListFragment : Fragment(), CryptoListAdapter.CryptoListListener {

    private lateinit var viewModel: CryptoViewModel
    private var _binding: FragmentCryptoListBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    private lateinit var cryptoRecyclerView: RecyclerView
    private var showToast = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(activity).application
        viewModel =
            ViewModelProvider(this, ViewModelFactory(application))[CryptoViewModel::class.java]

        cryptoRecyclerView = binding.cryptoList
        val loadingProgressBar = binding.loading

        cryptoRecyclerView.adapter = CryptoListAdapter(null, this)
        cryptoRecyclerView.layoutManager = LinearLayoutManager(activity)
        cryptoRecyclerView.addItemDecoration(
            DividerItemDecoration(
                cryptoRecyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        cryptoRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER

        viewModel.fetchCrCsResult.observe(viewLifecycleOwner,
            Observer { singleEvent ->
                singleEvent ?: return@Observer
                showToast = !singleEvent.hasBeenHandled
                val result = singleEvent.getContent()
                loadingProgressBar.visibility = View.GONE

                result?.onSuccess { showList(it) }
                result?.onFailure { showFetchFailed(it.toString()) }
            })

        if (viewModel.fetchCrCsResult.value == null) {
            viewModel.fetchCrCs()
        }
    }

    private fun showList(result: CryptoCurrencyList?) {
        val appContext = context?.applicationContext ?: return
        result?.let {
            it.data?.let { data ->
                cryptoRecyclerView.adapter = CryptoListAdapter(data.values, this)
            }
            if (showToast) {
                Toast.makeText(appContext, "${it.response}! ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showFetchFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun listItemClicked(position: Int) {
        view?.let {
            val action =
                CryptoListFragmentDirections.actionCryptoListFragmentToCryptoDetailsFragment(
                    position
                )
            it.findNavController().navigate(action)
        }
    }
}