package rs.dk150.cryptotracker.ui

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.HistoricalList
import rs.dk150.cryptotracker.databinding.FragmentGraphBinding
import rs.dk150.cryptotracker.model.CryptoViewModel

/**
 * Fragment defining second tab of second page, showing historical
 * price value graph of selected cryptocurrency
 */
class GraphFragment : Fragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var viewModel: CryptoViewModel

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    // historical value time unit views
    private lateinit var menuLabel: View
    private lateinit var menu: View
    private lateinit var textField: AutoCompleteTextView

    private lateinit var divider: View

    // time range views
    private lateinit var radioGroup: RadioGroup

    private lateinit var graph: SurfaceView
    private lateinit var loadingProgressBar: View
    private var surface: SurfaceHolder? = null

    /* position value is received as an argument and used to identify
       selected cryptoCurrency */
    private var position: Int = RecyclerView.NO_POSITION

    // flag specifying whether to show toast message about fetch result
    private var sentRequest = false
    private val graphDrawer by lazy {
        GraphDrawer(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(DetailsFragment.POSITION_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
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
        textField = binding.textField

        divider = binding.divider

        radioGroup = binding.radioGroup

        graph = binding.graph
        loadingProgressBar = binding.loading

        // hide time unit views
        setUnitViewsVisibility(false)

        // set up dropdown menu
        val list = resources.getStringArray(R.array.time_ranges).toList()
        val adapter = DropdownAdapter(
            requireContext(),
            R.layout.dropdown_item,
            list
        )
        textField.also {
            it.setAdapter(adapter)
            it.setText(list[1], false)
            it.doOnTextChanged { text, _, _, _ ->
                // time unit changed
                val txt = text.toString()
                toggleEnabledButtons(txt)
                val array = resources.getStringArray(R.array.time_ranges)
                toggleTimeRange(txt, array)
                // draw graph if data for selected time unit available, or else fetch data
                tryDraw(txt, array)
            }
        }

        // set up radio buttons
        toggleEnabledButtons(textField.text.toString())
        radioGroup.check(R.id.oneD)
        radioGroup.setOnCheckedChangeListener(this)

        // set up graph
        graph.holder.addCallback(object : SurfaceHolder.Callback {
            /* called whenever canvas is ready for drawing (surface view
               that holds it becomes visible) */
            override fun surfaceCreated(p0: SurfaceHolder) {
                surface = p0
                // to prevent black flash
                val canvas = surface?.lockCanvas()
                canvas?.drawColor(Color.WHITE)
                surface?.unlockCanvasAndPost(canvas)
                val array = resources.getStringArray(R.array.time_ranges)
                val historicalList: List<HistoricalList.Value>? =
                    when (textField.text.toString()) {
                        array[0] -> viewModel.crCsHistoricalMResult.value?.getOrNull()?.data?.data
                        array[1] -> viewModel.crCsHistoricalHResult.value?.getOrNull()?.data?.data
                        else -> viewModel.crCsHistoricalDResult.value?.getOrNull()?.data?.data
                    }
                historicalList?.let {
                    drawGraph(surface, it)
                }
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                surface = null
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }
        })

        // add viewModel live data observers
        addObservers()

        // fetch data from viewModel
        if (viewModel.crCsHistoricalHResult.value == null) {
            sentRequest = true
            viewModel.fetchCrCsHistoricalH(position)
        }
    }

    private fun setUnitViewsVisibility(b: Boolean) {
        val visibility = if (b) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        menuLabel.visibility = visibility
        menu.visibility = visibility
        divider.visibility = visibility
        radioGroup.visibility = visibility
        graph.visibility = visibility
    }

    // when time unit changed, toggle enabled time range buttons
    private fun toggleEnabledButtons(timeRange: String) {
        // one hour, three hours, one day, three days, one week, two weeks, one month
        val enabled = List(7) {
            false
        }.toMutableList()
        val array = resources.getStringArray(R.array.time_ranges)
        when (timeRange) {
            array[0] -> {
                // MINS: one hour, three hours, one day
                for (i in 0..2) {
                    enabled[i] = true
                }
            }
            array[1] -> {
                //HRS: one day, three days, one week
                for (i in 2..4) {
                    enabled[i] = true
                }
            }
            else -> {
                //DAYS: !one day???, one week, two weeks, one month
                //enabled[2] = true
                for (i in 4..6) {
                    enabled[i] = true
                }
            }
        }
        for ((i, e) in enabled.withIndex()) {
            radioGroup.getChildAt(i).isEnabled = e
        }
    }

    private fun toggleTimeRange(text: String, array: Array<String>) {
        if (!radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId).isEnabled) {
            // currently selected time range is not available for newly selected time unit
            radioGroup.setOnCheckedChangeListener(null)
            // set default time range for selected time unit
            when (text) {
                array[0] -> {
                    radioGroup.check(R.id.oneH)
                }
                array[1] -> {
                    radioGroup.check(R.id.oneD)
                }
                else -> {
                    radioGroup.check(R.id.oneW)
                }
            }
            radioGroup.setOnCheckedChangeListener(this)
        }
    }

    private fun tryDraw(text: String, array: Array<String>) {
        // get historical price data from ViewModel
        var symbol: String? = null
        var res: Result<HistoricalList?>? = null
        val func =
            when (text) {
                array[0] -> {
                    symbol = viewModel.crCsHistoricalSymbolM
                    res = viewModel.crCsHistoricalMResult.value
                    CryptoViewModel::fetchCrCsHistoricalM
                }
                array[1] -> {
                    symbol = viewModel.crCsHistoricalSymbolH
                    res = viewModel.crCsHistoricalHResult.value
                    CryptoViewModel::fetchCrCsHistoricalH
                }
                else -> {
                    symbol = viewModel.crCsHistoricalSymbolD
                    res = viewModel.crCsHistoricalDResult.value
                    CryptoViewModel::fetchCrCsHistoricalD
                }
            }
        val historicalList: List<HistoricalList.Value>? =
            if (viewModel.getSymbol(position)
                    .equals(symbol)
            ) {
                res?.getOrNull()?.data?.data
            } else {
                null
            }
        historicalList.let { hL ->
            if (hL != null) {
                // if data available, draw graph
                drawGraph(surface, hL)
            } else {
                // else, fetch data
                graph.visibility = View.INVISIBLE
                loadingProgressBar.visibility = View.VISIBLE
                sentRequest = true
                func(viewModel, position)
            }
        }
    }

    private fun drawGraph(surface: SurfaceHolder?, values: List<HistoricalList.Value>) {
        // determine how many (time, value) points to draw
        val points = when (radioGroup.checkedRadioButtonId) {
            R.id.oneH -> 60
            R.id.threeH -> 180
            R.id.oneD -> {
                val array = resources.getStringArray(R.array.time_ranges)
                when (textField.text.toString()) {
                    array[0] -> 1440
                    else -> 24
                }
            }
            R.id.threeD -> 72
            R.id.oneW -> {
                val array = resources.getStringArray(R.array.time_ranges)
                when (textField.text.toString()) {
                    array[1] -> 168
                    else -> 7
                }
            }
            R.id.twoW -> 14
            else -> 30
        }
        surface?.let {
            MainScope().launch {
                val canvas = it.lockCanvas()
                graphDrawer.draw(canvas, values, points)
                it.unlockCanvasAndPost(canvas)
            }
        }
    }

    // when time range changed, draw another graph
    override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
        val array = resources.getStringArray(R.array.time_ranges)
        val historicalList: List<HistoricalList.Value>? =
            when (textField.text.toString()) {
                array[0] -> viewModel.crCsHistoricalMResult.value?.getOrNull()?.data?.data
                array[1] -> viewModel.crCsHistoricalHResult.value?.getOrNull()?.data?.data
                else -> viewModel.crCsHistoricalDResult.value?.getOrNull()?.data?.data
            }
        historicalList?.let {
            drawGraph(surface, it)
        }
    }

    private fun addObservers() {
        viewModel.crCsHistoricalHResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                if (textField.text.toString() != "HRS") {
                    return@Observer
                }
                result.onSuccess {
                    if (viewModel.getSymbol(position).equals(viewModel.crCsHistoricalSymbolH)) {
                        showViews(it)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalH(position)
                    }
                }
                result.onFailure {
                    if (sentRequest) {
                        showFetchFailed(it.message)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalH(position)
                    }

                }
            })
        viewModel.crCsHistoricalMResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                if (textField.text.toString() != "MINS") {
                    return@Observer
                }
                result.onSuccess {
                    if (viewModel.getSymbol(position).equals(viewModel.crCsHistoricalSymbolM)) {
                        showViews(it)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalM(position)
                    }
                }
                result.onFailure {
                    if (sentRequest) {
                        showFetchFailed(it.message)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalM(position)
                    }

                }
            })
        viewModel.crCsHistoricalDResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                if (textField.text.toString() != "DAYS") {
                    return@Observer
                }
                result.onSuccess {
                    if (viewModel.getSymbol(position).equals(viewModel.crCsHistoricalSymbolD)) {
                        showViews(it)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalD(position)
                    }
                }
                result.onFailure {
                    if (sentRequest) {
                        showFetchFailed(it.message)
                    } else {
                        sentRequest = true
                        viewModel.fetchCrCsHistoricalD(position)
                    }

                }
            })
    }

    private fun showViews(result: HistoricalList?) {
        loadingProgressBar.visibility = View.INVISIBLE
        result?.let {
            /* val appContext = context?.applicationContext ?: return
            Toast.makeText(
                appContext,
                "${result.response}! Coin historical price data successfully returned!",
                Toast.LENGTH_LONG
            ).show()*/
            setUnitViewsVisibility(true)
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