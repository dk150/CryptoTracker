package rs.dk150.cryptotracker.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import rs.dk150.cryptotracker.R
import rs.dk150.cryptotracker.data.CryptoCurrencyList
import rs.dk150.cryptotracker.databinding.GeneralImageViewHolderBinding
import rs.dk150.cryptotracker.databinding.GeneralTextViewHolderBinding


/**
 * Adapter for list of general info(details) about selected cryptocurrency
 */
class GeneralListAdapter(
    private val list: List<CryptoCurrencyList.CryptoCurrency.CryptoField>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewHolderType {
        TEXT, IMAGE
    }

    override fun getItemViewType(position: Int): Int {
        return if (list?.get(position)?.label == "imageUrl") {
            ViewHolderType.IMAGE.ordinal
        } else {
            ViewHolderType.TEXT.ordinal
        }
    }

    /**
     * Class defining TEXT ViewHolder for detail info about cryptocurrency
     */
    class GeneralTextViewHolder(binding: GeneralTextViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val valueLabel: TextView = binding.valueLabel
        val value: TextView = binding.value

    }

    /**
     * Class defining IMAGE ViewHolder for detail info about cryptocurrency
     */
    class GeneralImageViewHolder(binding: GeneralImageViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val value: ImageView = binding.image
        val progress: View = binding.progress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /* heterogeneous viewHolders because imageView(for symbol image) replaces textView (for other
           attributes) */
        return when (ViewHolderType.values()[viewType]) {
            ViewHolderType.TEXT -> GeneralTextViewHolder(
                GeneralTextViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            ViewHolderType.IMAGE -> GeneralImageViewHolder(
                GeneralImageViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        list?.let {
            val pos = viewHolder.bindingAdapterPosition
            val element = list[pos]
            when (ViewHolderType.values()[viewHolder.itemViewType]) {
                ViewHolderType.TEXT -> {
                    val holder = viewHolder as GeneralTextViewHolder
                    val valueLabel = holder.valueLabel
                    // set textView values
                    valueLabel.text =
                        String.format(
                            valueLabel.context.getString(R.string.value_label),
                            element.label
                        )
                    val regex = "url".toRegex(RegexOption.IGNORE_CASE)
                    holder.value.text = if(regex.containsMatchIn(element.label)) {
                        formatUrlValue(element.value)
                    } else {
                        element.value
                    }
                }
                ViewHolderType.IMAGE -> {
                    val holder = viewHolder as GeneralImageViewHolder
                    // lazy loading of symbol image
                    val imageView = holder.value
                    val progressBar = holder.progress
                    var requestBuilder =
                        Glide.with(imageView.context)
                            .load("https://cryptocompare.com" + element.value)
                    requestBuilder = requestBuilder.listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // set placeholder image
                            progressBar.visibility = View.INVISIBLE
                            imageView.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    imageView.context,
                                    R.drawable.placeholder_img
                                )
                            )
                            imageView.visibility = View.VISIBLE
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.INVISIBLE
                            imageView.visibility = View.VISIBLE
                            return false
                        }

                    })
                    requestBuilder.centerInside().into(imageView)
                }
            }
        }
    }

    private fun formatUrlValue(value: String): CharSequence {
        val v = value.trim()
        return if (v[0] == '/') {
            "http://www.cryptocompare.com${v}"
        } else {
            return v
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0

}