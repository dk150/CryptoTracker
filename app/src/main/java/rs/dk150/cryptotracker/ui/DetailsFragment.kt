package rs.dk150.cryptotracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.databinding.FragmentDetailsBinding

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Fragment defining second page showing two tabs with
 * details about selected cryptocurrency (general info
 * tab & graph view tab)
 */
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    /* position value is received as an argument and used to identify
       selected cryptoCurrency */
    private var position: Int = RecyclerView.NO_POSITION

    // 2 tabs
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter

    companion object {
        private const val NUM_PAGES = 2
        const val POSITION_ARG = "position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = DetailsFragmentArgs.fromBundle(it)
            position = args.position
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up viewPager
        viewPager = binding.pager
        val tabLayout = binding.tabLayout
        pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "General Info"
                    tab.icon = AppCompatResources.getDrawable(viewPager.context, R.drawable.info)
                }
                1 -> {
                    tab.text = "Graph View"
                    tab.icon = AppCompatResources.getDrawable(viewPager.context, R.drawable.graph)
                }
            }
        }.attach()
    }

    /**
     * Adapter for ViewPager
     */
    private inner class ScreenSlidePagerAdapter(fa: Fragment?) :
        FragmentStateAdapter(fa!!) {
        override fun createFragment(pagePosition: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt(POSITION_ARG, position)
            return if(pagePosition==0) {
                val fragment = GeneralListFragment()
                fragment.arguments = bundle
                fragment
            } else {
                val fragment = GraphFragment()
                fragment.arguments = bundle
                fragment
            }
        }

        override fun getItemCount(): Int {
            return NUM_PAGES
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}