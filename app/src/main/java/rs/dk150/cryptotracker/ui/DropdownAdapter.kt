package rs.dk150.cryptotracker.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter

/** Adapter for material exposed dropDown menu which is implemented
 * as textInputLayout wrapping autoCompleteTextView(aCTV); This class
 * provides empty implementation of filter for aCTV to stop filtering
 * of menu items based on current text in aCTV and also turns off
 * overScroll effect on menu
 * https://material.io/components/menus/android#exposed-dropdown-menus
 * https://rmirabelle.medium.com/there-is-no-material-design-spinner-for-android-3261b7c77da8
 */
class DropdownAdapter<String>(context: Context, layout: Int, var values: List<String>) :
    ArrayAdapter<String>(context, layout, values) {
    private val noopFilter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return noopFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        parent.overScrollMode = View.OVER_SCROLL_NEVER
        return super.getView(position, convertView, parent)
    }
}