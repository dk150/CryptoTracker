package rs.dk150.cryptotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.databinding.FragmentBasicListBinding
import rs.dk150.cryptotracker.model.CryptoViewModel

/**
 * Fragment defining first page showing list of all cryptocurrencies with
 * their basic(summary) attributes
 */
class BasicListFragment : Fragment(), BasicListAdapter.CryptoListListener {

    private lateinit var viewModel: CryptoViewModel

    private var _binding: FragmentBasicListBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    private lateinit var cryptoRecyclerView: RecyclerView
    //private var showToast = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(activity).application
        viewModel =
            ViewModelProvider(this.requireActivity(), ViewModelFactory(application))[CryptoViewModel::class.java]

        cryptoRecyclerView = binding.cryptoList
        val loadingProgressBar = binding.loading

        // set up recyclerView
        cryptoRecyclerView.also{
            it.adapter = BasicListAdapter(null, this)
            it.layoutManager = LinearLayoutManager(activity)
            val drawable = AppCompatResources.getDrawable(it.context, R.drawable.divider)
            if(drawable!=null) {
                it.addItemDecoration(
                    DividerItemDecorator(
                        drawable
                    )
                )
            }
        }

        // add viewModel live data observers
        viewModel.crCsResult.observe(viewLifecycleOwner,
            Observer { singleEvent ->
                singleEvent ?: return@Observer
                //showToast = !singleEvent.hasBeenHandled
                val result = singleEvent.getContent()
                loadingProgressBar.visibility = View.GONE

                result?.onSuccess { showList(it) }
                result?.onFailure { showFetchFailed(it.message) }
            })

        // fetch data from viewModel
        if (viewModel.crCsResult.value == null) {
            viewModel.fetchCrCs()
        }
    }

    private fun showList(result: CryptoCurrencyList?) {
        result?.let {
            it.data?.let { data ->
                cryptoRecyclerView.adapter = BasicListAdapter(data.values.toList(), this)
            }
            /*if (showToast) {
                val appContext = context?.applicationContext ?: return
                Toast.makeText(appContext, "${it.response}! ${it.message}", Toast.LENGTH_LONG)
                    .show()
            }*/
        }
    }

    private fun showFetchFailed(errorString: String?) {
        Toast.makeText(this.context, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun listItemClicked(position: Int) {
        view?.let {
            // navigate to second page fragment
            val action =
                BasicListFragmentDirections.actionBasicListFragmentToDetailsFragment(
                    position
                )
            it.findNavController().navigate(action)
        }
    }
}