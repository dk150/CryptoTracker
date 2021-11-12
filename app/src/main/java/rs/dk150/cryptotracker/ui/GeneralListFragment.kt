package rs.dk150.cryptotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.data.CurrencyConversion
import rs.dk150.cryptotracker.databinding.FragmentGeneralListBinding
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

import androidx.appcompat.content.res.AppCompatResources.getDrawable
import rs.dk150.cryptotracker.model.CryptoViewModel

/**
 * Fragment defining first tab of second page, showing general info
 * of selected cryptocurrency including its value converted to selected
 * comparison currency & list of its details(additional) attributes
 */
class GeneralListFragment : Fragment() {

    private lateinit var viewModel: CryptoViewModel

    private var _binding: FragmentGeneralListBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    // currency conversion views
    private lateinit var menuLabel: View
    private lateinit var menu: View
    private lateinit var textField: AutoCompleteTextView
    private lateinit var doubleDot: View
    private lateinit var conversionValueTextView: TextView

    private lateinit var divider: View

    private lateinit var cryptoRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: View

    /* position value is received as an argument and used to identify
       selected cryptoCurrency */
    private var position: Int = RecyclerView.NO_POSITION
    // flag specifying whether to show toast message about fetch result
    private var sentRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            position = it.getInt(DetailsFragment.POSITION_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(activity).application
        viewModel =
            ViewModelProvider(
                this.requireActivity(),
                ViewModelFactory(application)
            )[CryptoViewModel::class.java]
        menuLabel = binding.menuLabel
        menu = binding.menu
        doubleDot = binding.doubleDot
        textField = binding.textField
        conversionValueTextView = binding.value

        divider = binding.divider

        cryptoRecyclerView = binding.list
        loadingProgressBar = binding.loading

        // hide currency conversion views
        setConversionViewsVisibility(false)

        // set up dropdown menu
        val list = viewModel.conversionCurrencies
        val dropDownAdapter = DropdownAdapter(
            requireContext(),
            R.layout.dropdown_item,
            list
        )
        textField.apply {
            setAdapter(dropDownAdapter)
            setText(list[0], false)
            doOnTextChanged { text, _, _, _ ->
                updateValueTextView(text.toString())
            }
        }

        // set up recyclerView
        cryptoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GeneralListAdapter(null)
            val drawable = getDrawable(context, R.drawable.divider)
            if(drawable!=null) {
                addItemDecoration(
                    DividerItemDecorator(
                        drawable
                    )
                )
            }
        }

        // add viewModel live data observers
        viewModel.crCsDetailsResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                result.onSuccess {
                    /* live data value is not cleared, so we need to check if the last fetched
                       data was for currently selected currency */
                    val symbol = it?.data?.values?.elementAtOrNull(0)?.symbol
                    if (viewModel.getSymbol(position).equals(symbol)) {
                        showList(it)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsDetails(position)
                    }
                }
                result.onFailure {
                    if(sentRequest) {
                        showFetchFailed(it.message)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsDetails(position)
                    }
                }
            })

        // fetch data from viewModel
        if (viewModel.crCsDetailsResult.value == null) {
            sentRequest = true
            viewModel.fetchCrCsDetails(position)
        }
    }

    private fun updateValueTextView(text: String) {
        viewModel.crCsConversionResult?.getOrNull()?.let{
            // get value of field whose name is the same as text
            val value = (CurrencyConversion::class.memberProperties
                .first { property -> property.name.equals(text, true) } as KProperty1<Any, *>).get(it)
            conversionValueTextView.text = value?.toString() ?: ""
        }
    }

    private fun setConversionViewsVisibility(b: Boolean) {
        val visibility = if (b) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        menuLabel.visibility = visibility
        menu.visibility = visibility
        doubleDot.visibility = visibility
        conversionValueTextView.visibility = visibility
        divider.visibility = visibility
    }

    private fun showList(result: CryptoCurrencyList?) {
        loadingProgressBar.visibility = View.INVISIBLE
        result?.let {
            it.data?.let { data ->
                cryptoRecyclerView.adapter =
                    GeneralListAdapter(data.values.elementAt(0).getFieldValues())
            }
            updateValueTextView(textField.editableText.toString())
            /*if (sentRequest) {
                val appContext = context?.applicationContext ?: return
                Toast.makeText(
                    appContext,
                    "${it.response}! Coin details successfully returned!",
                    Toast.LENGTH_LONG
                )
                    .show()
            }*/
            setConversionViewsVisibility(true)
        }
    }

    private fun showFetchFailed(errorString: String?) {
        loadingProgressBar.visibility = View.INVISIBLE
        Toast.makeText(this.context, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
